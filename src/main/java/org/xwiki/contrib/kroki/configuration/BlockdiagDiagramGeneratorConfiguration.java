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
package org.xwiki.contrib.kroki.configuration;

import java.util.Arrays;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Diagram generator configuration options for Blockdiag container.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named("blockdiag-config")
public class BlockdiagDiagramGeneratorConfiguration extends DefaultDiagramGeneratorConfiguration
{
    @Override
    public String getKrokiDockerImage()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBlockdiagDockerImage", "yuzutech/kroki-blockdiag");
    }

    @Override
    public String getKrokiDockerContainerName()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBlockdiagDockerContainerName",
            "kroki-blockdiag-container");
    }

    @Override
    public boolean isKrokiDockerContainerReusable()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBlockdiagDockerContainerReusable", true);
    }

    @Override
    public int getKrokiRemoteDebuggingPort()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBlockdiagRemoteDebuggingPort", 8001);
    }

    @Override
    public HealthCheckRequestParameters getHealthCheckRequest()
    {
        return new HealthCheckRequestParameters("/blockdiag/svg", "blockdiag {\n" + "   A;\n" + "}", "POST",
            Arrays.asList(200, 201));
    }
}
