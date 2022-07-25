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

import java.util.Arrays;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Diagram generator configuration options for Excalidraw container.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named("excalidraw-config")
public class ExcalidrawKrokiMacroConfiguration extends DefaultKrokiMacroConfiguration
{
    @Override
    public String getKrokiDockerImage()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiExcalidrawDockerImage", "yuzutech/kroki-excalidraw");
    }

    @Override
    public String getKrokiDockerContainerName()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiExcalidrawDockerContainerName",
            "kroki-excalidraw-container");
    }

    @Override
    public boolean isKrokiDockerContainerReusable()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiExcalidrawDockerContainerReusable", true);
    }

    @Override
    public int getKrokiPort()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiExcalidrawRemoteDebuggingPort", 8004);
    }

    @Override
    public HealthCheckRequestParameters getHealthCheckRequest()
    {
        return new HealthCheckRequestParameters("/excalidraw/svg",
            "{\n" + "  \"type\": \"excalidraw\",\n" + "  \"version\": 2,\n"
                + "  \"source\": \"https://excalidraw.com\",\n" + "  \"elements\": [\n" + "  ],\n"
                + "  \"appState\": {\n" + "    \"gridSize\": null,\n" + "    \"viewBackgroundColor\": \"#ffffff\"\n"
                + "  },\n" + "  \"files\": {}\n" + "}", "POST", Arrays.asList(200, 201));
    }
}
