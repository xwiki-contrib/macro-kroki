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

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Diagram generator configuration options for the main container.
 *
 * @version $Id$
 */
@Component
@Singleton
public class DefaultKrokiMacroConfiguration implements KrokiMacroConfiguration
{
    protected static final String PREFIX = "contrib.krokiMacro";

    @Inject
    @Named("xwikiproperties")
    protected ConfigurationSource configurationSource;

    @Override
    public String getKrokiDockerImage()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiDockerImage", "yuzutech/kroki");
    }

    @Override
    public String getKrokiDockerContainerName()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiDockerContainerName", "kroki-diagram-generator");
    }

    @Override
    public boolean isKrokiDockerContainerReusable()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiDockerContainerReusable", false);
    }

    @Override
    public String getKrokiHost()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiHost", "");
    }

    @Override
    public int getKrokiPort()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiRemoteDebuggingPort", 8000);
    }

    @Override
    public HealthCheckRequestParameters getHealthCheckRequest()
    {
        return new HealthCheckRequestParameters("", "", "GET", Collections.singletonList(200));
    }
}
