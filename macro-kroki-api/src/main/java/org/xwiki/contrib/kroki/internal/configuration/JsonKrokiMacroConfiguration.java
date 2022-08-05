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

import java.util.List;
import java.util.Map;

import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Macro configuration instantiated from Json.
 *
 * @version $Id$
 */
public class JsonKrokiMacroConfiguration implements KrokiMacroConfiguration
{
    private static final String PREFIX = "contrib.krokiMacro";

    private final ConfigurationSource configurationSource;

    private final String configFileName;

    private final String image;

    private final String containerName;

    private final boolean isContainerReusable;

    private final String host;

    private final int port;

    private final HealthCheckRequestParameters healthCheckRequestParameters;

    /**
     * Constructor for initiating configuration parameters.
     * @param configFileName the filename for the configuration file
     * @param configParameters default parameters if not specified in another configuration source
     * @param configurationSource xwiki configuration source
     */
    public JsonKrokiMacroConfiguration(String configFileName,
        Map<String, Object> configParameters, ConfigurationSource configurationSource)
    {
        this.configFileName = configFileName;
        this.configurationSource = configurationSource;
        this.image = (String) configParameters.get("image");
        this.containerName = (String) configParameters.get("containerName");
        this.isContainerReusable = (boolean) configParameters.get("isContainerReusable");
        this.host = (String) configParameters.get("host");
        this.port = (int) configParameters.get("port");
        Map<String, Object> healthCheckParameters =
            (Map<String, Object>) configParameters.get("healthCheckRequestParameters");
        this.healthCheckRequestParameters = new HealthCheckRequestParameters((String) healthCheckParameters.get("path"),
            (String) healthCheckParameters.get("body"), (String) healthCheckParameters.get("httpVerb"),
            (List<Integer>) healthCheckParameters.get(
                "acceptedStatusCodes"));
    }

    @Override
    public String getKrokiDockerImage()
    {
        return this.configurationSource.getProperty(PREFIX + configFileName + "DockerImage", image);
    }

    @Override
    public String getKrokiDockerContainerName()
    {
        return this.configurationSource.getProperty(PREFIX + configFileName + "DockerContainerName", containerName);
    }

    @Override
    public boolean isKrokiDockerContainerReusable()
    {
        return this.configurationSource.getProperty(PREFIX + configFileName + "DockerContainerReusable",
            isContainerReusable);
    }

    @Override
    public String getKrokiHost()
    {
        return this.configurationSource.getProperty(PREFIX + configFileName + "Host", host);
    }

    @Override
    public int getKrokiPort()
    {
        return this.configurationSource.getProperty(PREFIX + configFileName + "Port", port);
    }

    @Override
    public HealthCheckRequestParameters getHealthCheckRequest()
    {
        return healthCheckRequestParameters;
    }
}
