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
package org.xwiki.contrib.kroki.generator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.component.phase.Disposable;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.contrib.kroki.configuration.DiagramGeneratorConfiguration;
import org.xwiki.contrib.kroki.docker.ContainerManager;

import com.github.dockerjava.api.model.HostConfig;

/**
 * Generates a diagram from text using the Kroki API running in Docker or provided as a service.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named("docker-kroki")
public class KrokiDiagramGenerator implements DiagramGenerator, Initializable, Disposable
{
    private final Map<String, String> containerIds = new HashMap<>();

    @Inject
    private KrokiConnectionManager krokiManager;

    @Inject
    private Logger logger;

    @Inject
    @Named("default-config")
    private DiagramGeneratorConfiguration configuration;

    @Inject
    @Named("blockdiag-config")
    private DiagramGeneratorConfiguration configurationBlockdiag;

    @Inject
    @Named("bpmn-config")
    private DiagramGeneratorConfiguration configurationBpmn;

    @Inject
    @Named("excalidraw-config")
    private DiagramGeneratorConfiguration configurationExcalidraw;

    @Inject
    @Named("mermaid-config")
    private DiagramGeneratorConfiguration configurationMermaid;

    /**
     * We use a provider (i.e. lazy initialization) because we don't always need this component (e.g. when the PDF
     * export is done through a hosted kroki api that is not managed by XWiki).
     */
    @Inject
    private Provider<ContainerManager> containerManagerProvider;

    @Override
    public void initialize() throws InitializationException
    {
        initializeService(this.configuration);
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
    public InputStream generateDiagram(String diagramLib, String imgFormat, String graphContent)
    {
        if (StringUtils.isBlank(diagramLib)) {
            throw new IllegalArgumentException("The diagram library to use is missing");
        } else if (StringUtils.isBlank(imgFormat)) {
            throw new IllegalArgumentException("The format of the image is missing");
        } else if (StringUtils.isBlank(graphContent)) {
            throw new IllegalArgumentException("The content of the graph is missing");
        }

        DiagramGeneratorConfiguration libConfig = getLibraryConfiguration(diagramLib);

        try {
            initializeService(libConfig);
        } catch (InitializationException e) {
            throw new RuntimeException(e);
        }

        return krokiManager.generateDiagram(diagramLib, imgFormat, graphContent);
    }

    private DiagramGeneratorConfiguration getLibraryConfiguration(String diagramLib)
    {
        switch (diagramLib) {
            case ("blockdiag"):
            case ("seqdiag"):
            case ("actdiag"):
            case ("nwdiag"):
            case ("packetdiag"):
            case ("rackdiag"):
                return configurationBlockdiag;
            case ("mermaid"):
                return configurationMermaid;
            case ("bpmn"):
                return configurationBpmn;
            case ("excalidraw"):
                return configurationExcalidraw;
            default:
                return configuration;
        }
    }

    private String initializeKrokiDockerContainer(DiagramGeneratorConfiguration config) throws InitializationException
    {
        this.logger.debug("Initializing the Docker container running the Kroki API.");

        ContainerManager containerManager = this.containerManagerProvider.get();
        String imageName = config.getKrokiDockerImage();
        String containerName = config.getKrokiDockerContainerName();
        String network = config.getDockerNetwork();
        int remoteDebuggingPort = config.getKrokiRemoteDebuggingPort();
        String configName = config.getClass().getName();

        try {
            this.containerIds.put(configName,
                containerManager.maybeReuseContainerByName(containerName, config.isKrokiDockerContainerReusable()));
            if (this.containerIds.get(configName) == null) {
                // The container doesn't exist so we have to create it.
                // But first we need to pull the image used to create the container, if we don't have it already.
                if (!containerManager.isLocalImagePresent(imageName)) {
                    containerManager.pullImage(imageName);
                }

                HostConfig hostConfig = containerManager.getHostConfig(network, remoteDebuggingPort);
                if ("bridge".equals(network)) {
                    // The extra host is needed in order for the created container to be able to access the XWiki
                    // instance running on the same machine as the Docker daemon.
                    hostConfig = hostConfig.withExtraHosts(this.configuration.getXWikiHost() + ":host-gateway");
                }

                this.containerIds.put(configName,
                    containerManager.createContainer(imageName, containerName, new ArrayList<>(), hostConfig));
                containerManager.startContainer(this.containerIds.get(configName));
            }
            return containerManager.getIpAddress(this.containerIds.get(configName), network);
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize the Docker container for the graph generation.", e);
        }
    }

    private void initializeKrokiService(String host, DiagramGeneratorConfiguration config)
        throws InitializationException
    {
        try {
            this.krokiManager.setup(host, config);
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize the kroki remote debugging service.", e);
        }
    }

    private void initializeService(DiagramGeneratorConfiguration config) throws InitializationException
    {
        String krokiHost = config.getKrokiHost();
        if (StringUtils.isBlank(krokiHost)) {
            krokiHost = initializeKrokiDockerContainer(config);
        }
        initializeKrokiService(krokiHost, config);
    }
}
