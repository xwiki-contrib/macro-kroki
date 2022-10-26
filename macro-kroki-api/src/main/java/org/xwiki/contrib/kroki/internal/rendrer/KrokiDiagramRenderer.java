/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.kroki.internal.rendrer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.component.phase.Disposable;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.internal.configuration.KrokiConfiguration;
import org.xwiki.contrib.kroki.internal.docker.ContainerManager;
import org.xwiki.contrib.kroki.renderer.DiagramRenderer;

import com.github.dockerjava.api.model.HostConfig;

/**
 * Generates a diagram from text using the Kroki API running in Docker or provided as a service.
 *
 * @version $Id$
 */
@Component
@Singleton
public class KrokiDiagramRenderer implements DiagramRenderer, Initializable, Disposable
{
    private static final String TLS = "https";
    private static final String NO_TLS = "http";
    
    private final Map<String, String> containerIds = new HashMap<>();

    @Inject
    private KrokiService krokiService;

    @Inject
    private Logger logger;

    @Inject
    private KrokiConfiguration krokiConfiguration;

    /**
     * We use a provider (i.e. lazy initialization) because we don't always need this component (e.g. when the diagram
     * rendering is done through a hosted kroki api that is not managed by XWiki).
     */
    @Inject
    private Provider<ContainerManager> containerManagerProvider;

    @Override
    public void initialize() throws InitializationException
    {
        //plantuml is one of the diagram types supported by the default container
        initializeKrokiComponent(krokiConfiguration.getConfiguration("plantuml"));
    }

    @Override
    public void dispose() throws ComponentLifecycleException
    {
        Exception toThrow = null;
        String containerResponsible = "";
        for (String containerId : containerIds.values()) {
            if (containerId != null) {
                try {
                    this.containerManagerProvider.get().stopContainer(containerId);
                } catch (Exception e) {
                    toThrow = e;
                    containerResponsible = containerId;
                }
            }
        }

        if (toThrow != null) {
            throw new ComponentLifecycleException(
                String.format("Failed to stop the Docker container [%s] used for Kroki API.", containerResponsible),
                toThrow);
        }
    }

    @Override
    public InputStream render(String diagramType, String outputType, String diagramContent)
    {
        if (StringUtils.isBlank(diagramType)) {
            throw new IllegalArgumentException("The diagram library to use is missing");
        } else if (StringUtils.isBlank(outputType)) {
            throw new IllegalArgumentException("The format of the image is missing");
        } else if (StringUtils.isBlank(diagramContent)) {
            throw new IllegalArgumentException("The content of the graph is missing");
        }

        KrokiMacroConfiguration diagramConfig = krokiConfiguration.getConfiguration(diagramType);
        try {
            initializeKrokiComponent(diagramConfig);
        } catch (InitializationException e) {
            throw new RuntimeException(e);
        }

        return krokiService.renderDiagram(diagramType, outputType, diagramContent);
    }

    private String initializeKrokiDockerContainer(KrokiMacroConfiguration config) throws InitializationException
    {
        this.logger.debug("Initializing the Docker container running the Kroki API.");

        ContainerManager containerManager = this.containerManagerProvider.get();
        String imageName = config.getKrokiDockerImage();
        String containerName = config.getKrokiDockerContainerName();
        int port = config.getKrokiPort();
        String configName = config.getClass().getName();

        try {
            this.containerIds.put(configName,
                containerManager.maybeReuseContainerByName(containerName, config.isKrokiDockerContainerReusable()));
            if (this.containerIds.get(configName) == null) {
                // The container doesn't exist, so we have to create it.
                // But first we need to pull the image used to create the container, if we don't have it already.
                if (!containerManager.isLocalImagePresent(imageName)) {
                    containerManager.pullImage(imageName);
                }

                HostConfig hostConfig = containerManager.getHostConfig(port);

                this.containerIds.put(configName,
                    containerManager.createContainer(imageName, containerName, new ArrayList<>(), hostConfig));
                containerManager.startContainer(this.containerIds.get(configName));
            }
            return containerManager.getIpAddress(this.containerIds.get(configName));
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize the Docker container for diagram rendering.", e);
        }
    }

    private void initializeKrokiService(String host, KrokiMacroConfiguration config, String httpProtocol)
        throws InitializationException
    {
        try {
            this.krokiService.connect(host, config, httpProtocol);
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize the kroki remote debugging service.", e);
        }
    }

    private void initializeKrokiComponent(KrokiMacroConfiguration config) throws InitializationException
    {
        if (config == null) {
            throw new RuntimeException("There is no configuration defined for this type of diagram");
        }
        String httpProtocol = config.getKrokiUseTLS() ? TLS : NO_TLS;
        String krokiHost = config.getKrokiHost();
        if (StringUtils.isBlank(krokiHost)) {
            httpProtocol = NO_TLS;
            krokiHost = initializeKrokiDockerContainer(config);
        }
        initializeKrokiService(krokiHost, config, httpProtocol);
    }
}
