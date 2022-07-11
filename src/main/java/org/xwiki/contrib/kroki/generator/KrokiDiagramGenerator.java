package org.xwiki.contrib.kroki.generator;

import com.github.dockerjava.api.model.HostConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.component.phase.Disposable;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.contrib.kroki.configuration.DiagramGeneratorConfiguration;
import org.xwiki.contrib.kroki.docker.ContainerManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Singleton
@Named("docker-kroki")
public class KrokiDiagramGenerator implements DiagramGenerator, Initializable, Disposable{
    @Inject
    private Logger logger;

    @Inject
    @Named("default-config")
    private DiagramGeneratorConfiguration configuration;

    @Inject
    KrokiConnectionManager krokiManager;

    /**
     * We use a provider (i.e. lazy initialization) because we don't always need this component (e.g. when the PDF
     * export is done through a hosted kroki api that is not managed by XWiki).
     */
    @Inject
    private Provider<ContainerManager> containerManagerProvider;

    private String containerId;

    @Override
    public void initialize() throws InitializationException {
        String krokiHost = this.configuration.getKrokiHost();
        if (StringUtils.isBlank(krokiHost)) {
            krokiHost = initializeKrokiDockerContainer(this.configuration.getKrokiDockerImage(),
                    this.configuration.getKrokiDockerContainerName(), this.configuration.getDockerNetwork(),
                    this.configuration.getKrokiRemoteDebuggingPort());
        }
        initializeKrokiService(krokiHost, this.configuration.getKrokiRemoteDebuggingPort());
    }

    private String initializeKrokiDockerContainer(String imageName, String containerName, String network,
                                                  int remoteDebuggingPort) throws InitializationException
    {
        this.logger.debug("Initializing the Docker container running the Kroki API.");
        ContainerManager containerManager = this.containerManagerProvider.get();
        try {
            this.containerId = containerManager.maybeReuseContainerByName(containerName,
                    this.configuration.isKrokiDockerContainerReusable());
            if (this.containerId == null) {
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

                this.containerId = containerManager.createContainer(imageName, containerName,
                        Arrays.asList("--no-sandbox", "--remote-debugging-address=0.0.0.0",
                                "--remote-debugging-port=" + remoteDebuggingPort),
                        hostConfig);
                containerManager.startContainer(this.containerId);
            }
            return containerManager.getIpAddress(this.containerId, network);
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize the Docker container for the graph generation.", e);
        }
    }

    private void initializeKrokiService(String host, int remoteDebuggingPort) throws InitializationException
    {
        try {
            this.krokiManager.setup(host, remoteDebuggingPort);
        } catch (Exception e) {
            throw new InitializationException("Failed to initialize the kroki remote debugging service.", e);
        }
    }

    @Override
    public void dispose() throws ComponentLifecycleException {
        if (this.containerId != null) {
            try {
                this.containerManagerProvider.get().stopContainer(this.containerId);
            } catch (Exception e) {
                throw new ComponentLifecycleException(
                        String.format("Failed to stop the Docker container [%s] used for PDF export.", this.containerId),
                        e);
            }
        }
    }

    @Override
    public InputStream generateDiagram(String diagramLib, String imgFormat, String graphContent){
        InputStream result = krokiManager.generateDiagram(diagramLib, imgFormat, graphContent);
        return result;
    }
}
