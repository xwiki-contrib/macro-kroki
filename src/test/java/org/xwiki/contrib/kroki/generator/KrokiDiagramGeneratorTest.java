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

import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.kroki.configuration.DiagramGeneratorConfiguration;
import org.xwiki.contrib.kroki.docker.ContainerManager;
import org.xwiki.test.annotation.BeforeComponent;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.github.dockerjava.api.model.HostConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ComponentTest
class KrokiDiagramGeneratorTest
{
    private final String containerId = "8f55a905efec";

    private final String containerIpAddress = "172.17.0.2";

    HostConfig hostConfig;

    @InjectMockComponents
    private KrokiDiagramGenerator krokiDiagramGenerator;

    @MockComponent
    @Named("default-config")
    private DiagramGeneratorConfiguration configuration;

    @MockComponent
    private KrokiConnectionManager krokiManager;

    @MockComponent
    private ContainerManager containerManager;

    @BeforeComponent
    void configure()
    {
        when(this.configuration.getKrokiDockerContainerName()).thenReturn("test-kroki");
        when(this.configuration.getKrokiDockerImage()).thenReturn("yuzutech/kroki:latest");
        when(this.configuration.getKrokiRemoteDebuggingPort()).thenReturn(8000);
        when(this.configuration.getXWikiHost()).thenReturn("xwiki-host");

        mockNetwork("bridge", this.configuration);

        when(this.containerManager.createContainer(this.configuration.getKrokiDockerImage(),
            this.configuration.getKrokiDockerContainerName(),
            new ArrayList<>(),
            this.hostConfig)).thenReturn(this.containerId);
    }

    @BeforeComponent("initializeAndDispose")
    void beforeInitializeAndDispose()
    {
        when(this.containerManager.maybeReuseContainerByName(this.configuration.getKrokiDockerContainerName(), false))
            .thenReturn(null);
        when(this.containerManager.isLocalImagePresent(this.configuration.getKrokiDockerImage())).thenReturn(false);
    }

    @Test
    void initializeAndDispose() throws Exception
    {
        verify(this.containerManager).pullImage(this.configuration.getKrokiDockerImage());
        verify(this.containerManager).startContainer(this.containerId);
        verify(this.krokiManager).setup(this.containerIpAddress, this.configuration);

        this.krokiDiagramGenerator.dispose();
        verify(this.containerManager).stopContainer(this.containerId);
    }

    @BeforeComponent("initializeWithExistingContainer")
    void beforeInitializeWithExistingContainer()
    {
        mockNetwork("test-container", this.configuration);
        when(this.configuration.isKrokiDockerContainerReusable()).thenReturn(true);
        when(this.containerManager.maybeReuseContainerByName(this.configuration.getKrokiDockerContainerName(), true))
            .thenReturn(this.containerId);
    }

    @Test
    void initializeWithExistingContainer() throws Exception
    {
        verify(this.containerManager, never()).pullImage(any(String.class));
        verify(this.containerManager, never()).startContainer(any(String.class));
        verify(this.hostConfig, never()).withExtraHosts(any(String.class));
        verify(this.krokiManager).setup(this.containerIpAddress, this.configuration);

        this.krokiDiagramGenerator.dispose();
        verify(this.containerManager).stopContainer(this.containerId);
    }

    @BeforeComponent("initializeWithRemoteKroki")
    void beforeInitializeWithRemoteKroki()
    {
        when(this.configuration.getKrokiHost()).thenReturn("remote-kroki");
    }

    @Test
    void initializeWithRemoteKroki() throws Exception
    {
        verify(this.containerManager, never()).maybeReuseContainerByName(any(String.class), any(Boolean.class));
        verify(this.containerManager, never()).startContainer(any(String.class));

        verify(this.krokiManager).setup("remote-kroki", this.configuration);

        this.krokiDiagramGenerator.dispose();
        verify(this.containerManager, never()).stopContainer(any(String.class));
    }

    @Test
    void generateDiagramWithoutLibrary()
    {
        try {
            this.krokiDiagramGenerator.generateDiagram(null, "svg", "digraph G {Hello->World}");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The diagram library to use is missing", e.getMessage());
        }
    }

    @Test
    void generateDiagramWithoutImageFormat()
    {
        try {
            this.krokiDiagramGenerator.generateDiagram("graphviz", null, "digraph G {Hello->World}");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The format of the image is missing", e.getMessage());
        }
    }

    @Test
    void generateDiagramWithoutGraphContent()
    {
        try {
            this.krokiDiagramGenerator.generateDiagram("graphviz", "svg", null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The content of the graph is missing", e.getMessage());
        }
    }

    @Test
    void generateDiagram()
    {
        String diagramLibrary = "graphviz";
        String imgFormat = "svg";
        String graphContent = "digraph G {Hello->World}";

        InputStream diagramInputStream = mock(InputStream.class);

        when(this.krokiManager.generateDiagram(same(diagramLibrary), same(imgFormat), same(graphContent))).thenReturn(
            diagramInputStream);

        assertSame(diagramInputStream,
            this.krokiDiagramGenerator.generateDiagram(diagramLibrary, imgFormat, graphContent));
    }

    private void mockNetwork(String networkIdOrName, DiagramGeneratorConfiguration config)
    {
        when(config.getDockerNetwork()).thenReturn(networkIdOrName);
        when(this.containerManager.getIpAddress(this.containerId, networkIdOrName)).thenReturn(this.containerIpAddress);

        this.hostConfig = mock(HostConfig.class);
        when(this.containerManager.getHostConfig(networkIdOrName, config.getKrokiRemoteDebuggingPort()))
            .thenReturn(this.hostConfig);
        when(this.hostConfig.withExtraHosts(config.getXWikiHost() + ":host-gateway"))
            .thenReturn(this.hostConfig);
    }
}