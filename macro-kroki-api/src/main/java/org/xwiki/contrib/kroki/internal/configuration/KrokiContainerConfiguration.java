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

package org.xwiki.contrib.kroki.internal.configuration;

import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Generic Macro Configuration class.
 *
 * @version $Id$
 */
public class KrokiContainerConfiguration implements KrokiMacroConfiguration
{
    private static final String PREFIX = "contrib.krokiMacro";

    private final String image;

    private final String containerName;

    private final boolean isContainerReusable;

    private final boolean useTls;

    private final String host;

    private final Integer port;

    private final HealthCheckRequestParameters healthCheckRequestParameters;

    private final ConfigurationSource configurationSource;

    private String configName;

    /**
     * Constructor for initiating configuration parameters.
     *
     * @param image docker image to be instantiated if no host available
     * @param containerName docker container name
     * @param isContainerReusable false if a new docker container should be instantiated \ even if one with the same
     *     name exists
     * @param useTls true to use TLS to contact the specified host
     * @param host address to be called for the Kroki service
     * @param port port to be called for the Kroki service
     * @param healthCheckRequestParameters request parameters used to chekc if the Kroki service is online
     * @param configurationSource used to overwrite properties from xwiki.properties file
     */
    public KrokiContainerConfiguration(String image, String containerName, boolean isContainerReusable,
        boolean useTls, String host, Integer port, HealthCheckRequestParameters healthCheckRequestParameters,
        ConfigurationSource configurationSource)
    {
        this.configName = containerName;
        this.image = image;
        this.containerName = containerName;
        this.isContainerReusable = isContainerReusable;
        this.useTls = useTls;
        this.host = host;
        this.port = port;
        this.healthCheckRequestParameters = healthCheckRequestParameters;
        this.configurationSource = configurationSource;
    }

    /**
     * Set the configuration name to be used for setting properties form xwiki.properties.
     * @param configName configuration name to be set
     */
    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    @Override
    public String getKrokiDockerImage()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "DockerImage", image);
    }

    @Override
    public String getKrokiDockerContainerName()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "DockerContainerName", containerName);
    }

    @Override
    public boolean isKrokiDockerContainerReusable()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "DockerContainerReusable",
            isContainerReusable);
    }

    @Override
    public boolean getKrokiUseTls()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "UseTls", useTls);
    }

    @Override
    public String getKrokiHost()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "Host", host);
    }

    @Override
    public Integer getKrokiPort()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "Port", port);
    }

    @Override
    public HealthCheckRequestParameters getHealthCheckRequest()
    {
        return healthCheckRequestParameters;
    }
}
