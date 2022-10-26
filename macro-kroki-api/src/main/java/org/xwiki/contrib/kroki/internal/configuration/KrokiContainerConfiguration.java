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
public final class KrokiContainerConfiguration implements KrokiMacroConfiguration
{
    private static final String PREFIX = "contrib.krokiMacro";

    private final String image;

    private final String containerName;

    private final boolean isContainerReusable;

    private final boolean useTLS;

    private final String host;

    private final Integer port;

    private final HealthCheckRequestParameters healthCheckRequestParameters;

    private final ConfigurationSource configurationSource;

    private String configName;

    private KrokiContainerConfiguration(Builder builder)
    {
        this.configName = builder.containerName;
        this.image = builder.image;
        this.containerName = builder.containerName;
        this.isContainerReusable = builder.isContainerReusable;
        this.useTLS = builder.useTLS;
        this.host = builder.host;
        this.port = builder.port;
        this.healthCheckRequestParameters = builder.healthCheckRequestParameters;
        this.configurationSource = builder.configurationSource;
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
    public boolean getKrokiUseTLS()
    {
        return this.configurationSource.getProperty(PREFIX + configName + "UseTLS", useTLS);
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


    /**
     * Generic Macro Configuration class builder.
     *
     * @version $Id$
     */
    public static class Builder
    {
        private String image;
        private String containerName;
        private boolean isContainerReusable;
        private boolean useTLS;
        private String host;
        private Integer port;
        private HealthCheckRequestParameters healthCheckRequestParameters;
        private ConfigurationSource configurationSource;

        /**
         * Instantiate an empty builder.
         */
        public Builder()
        {
        }

        /**
         * Set the docker image to be instantiated if no host available.
         * @param image docker image
         * @return Builder instance
         */
        public Builder setImage(String image)
        {
            this.image = image;
            return this;
        }

        /**
         * Set the name of the docker container.
         * @param containerName name of the container
         * @return Builder instance
         */
        public Builder setContainerName(String containerName)
        {
            this.containerName = containerName;
            return this;
        }

        /**
         * Set the value indicating if the docker container is reusable.
         * @param isContainerReusable false if a new docker container should be instantiated \ even if one with the same
     *     name exists
         * @return Builder instance
         */
        public Builder setContainerReusable(boolean isContainerReusable)
        {
            this.isContainerReusable = isContainerReusable;
            return this;
        }

        /**
         * Set the value indicating whether the host (if specified) should be contacted through TLS.
         * @param useTLS true to use TLS
         * @return Builder instance
         */
        public Builder setTLS(boolean useTLS)
        {
            this.useTLS = useTLS;
            return this;
        }

        /**
         * Set the the host to address of the Kroki instance.
         * @param host the host of the Kroki instance
         * @return Builder instance
         */
        public Builder setHost(String host)
        {
            this.host = host;
            return this;
        }

        /**
         * Set the port of the Kroki instance.
         * @param port the port of the Kroki instance
         * @return Builder instance
         */
        public Builder setPort(Integer port)
        {
            this.port = port;
            return this;
        }

        /**
         * Set the request parameters used to check if the Kroki service is online.
         * @param healthCheckRequestParameters parameters used to check if the Kroki service is online
         * @return Builder instance
         */
        public Builder setHealthCheckRequestParameters(HealthCheckRequestParameters healthCheckRequestParameters)
        {
            this.healthCheckRequestParameters = healthCheckRequestParameters;
            return this;
        }

        /**
         * Set the source used to overwrite properties from xwiki.properties file.
         * @param configurationSource the configuration source
         * @return Builder instance
         */
        public Builder setConfigurationSource(ConfigurationSource configurationSource)
        {
            this.configurationSource = configurationSource;
            return this;
        }

        /**
         * Builds the class and returns the result.
         * @return built KrokiContainerConfiguration
         */
        public KrokiContainerConfiguration build()
        {
            return new KrokiContainerConfiguration(this);
        }
    }
}
