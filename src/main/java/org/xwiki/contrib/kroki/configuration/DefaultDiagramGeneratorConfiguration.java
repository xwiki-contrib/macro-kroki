package org.xwiki.contrib.kroki.configuration;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Component
@Singleton
@Named("default-config")
public class DefaultDiagramGeneratorConfiguration implements DiagramGeneratorConfiguration {
    private static final String PREFIX = "generate.";

    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource configurationSource;

    @Override
    public String getKrokiDockerImage() {
        return this.configurationSource.getProperty(PREFIX + "krokiDockerImage", "yuzutech/kroki");
    }

    @Override
    public String getKrokiDockerContainerName() {
        return this.configurationSource.getProperty(PREFIX + "krokiDockerContainerName",
                "kroki-1");
    }

    @Override
    public boolean isKrokiDockerContainerReusable() {
        return this.configurationSource.getProperty(PREFIX + "krokiDockerContainerReusable", false);
    }

    @Override
    public String getDockerNetwork() {
        return this.configurationSource.getProperty(PREFIX + "dockerNetwork", "bridge");
    }

    @Override
    public String getKrokiHost() {
        return this.configurationSource.getProperty(PREFIX + "krokiHost", "");
    }

    @Override
    public int getKrokiRemoteDebuggingPort() {
        return this.configurationSource.getProperty(PREFIX + "krokiRemoteDebuggingPort", 8000);
    }

    @Override
    public String getXWikiHost() {
        return this.configurationSource.getProperty(PREFIX + "xwikiHost", "host.xwiki.internal");
    }
}
