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
package org.xwiki.contrib.kroki.renderer;

import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.internal.docker.ContainerManager;
import org.xwiki.contrib.kroki.internal.rendrer.KrokiService;
import org.xwiki.contrib.kroki.internal.rendrer.KrokiDiagramRenderer;
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
class KrokiDiagramRendererTest
{
    private final String containerId = "8f55a905efec";

    private final String containerIpAddress = "172.17.0.2";

    HostConfig hostConfig;

    @InjectMockComponents
    private KrokiDiagramRenderer krokiDiagramRenderer;

    @MockComponent
    private KrokiMacroConfiguration configuration;

    @MockComponent
    private KrokiService krokiManager;

    @MockComponent
    private ContainerManager containerManager;

    @BeforeComponent
    void configure()
    {
        when(this.configuration.getKrokiDockerContainerName()).thenReturn("test-kroki");
        when(this.configuration.getKrokiDockerImage()).thenReturn("yuzutech/kroki:latest");
        when(this.configuration.getKrokiPort()).thenReturn(8000);

        mockNetwork(this.configuration);

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
        verify(this.krokiManager).connect(this.containerIpAddress, this.configuration);

        this.krokiDiagramRenderer.dispose();
        verify(this.containerManager).stopContainer(this.containerId);
    }

    @BeforeComponent("initializeWithExistingContainer")
    void beforeInitializeWithExistingContainer()
    {
        mockNetwork(this.configuration);
        when(this.configuration.isKrokiDockerContainerReusable()).thenReturn(true);
        when(this.containerManager.maybeReuseContainerByName(this.configuration.getKrokiDockerContainerName(), true))
            .thenReturn(this.containerId);
    }

    @Test
    void initializeWithExistingContainer() throws Exception
    {
        verify(this.containerManager, never()).pullImage(any(String.class));
        verify(this.containerManager, never()).startContainer(any(String.class));
        verify(this.krokiManager).connect(this.containerIpAddress, this.configuration);

        this.krokiDiagramRenderer.dispose();
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

        verify(this.krokiManager).connect("remote-kroki", this.configuration);

        this.krokiDiagramRenderer.dispose();
        verify(this.containerManager, never()).stopContainer(any(String.class));
    }

    @Test
    void generateDiagramWithoutLibrary()
    {
        try {
            this.krokiDiagramRenderer.render(null, "svg", "digraph G {Hello->World}");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The diagram library to use is missing", e.getMessage());
        }
    }

    @Test
    void generateDiagramWithoutImageFormat()
    {
        try {
            this.krokiDiagramRenderer.render("graphviz", null, "digraph G {Hello->World}");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The format of the image is missing", e.getMessage());
        }
    }

    @Test
    void generateDiagramWithoutGraphContent()
    {
        try {
            this.krokiDiagramRenderer.render("graphviz", "svg", null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The content of the graph is missing", e.getMessage());
        }
    }

    @Test
    void generateDiagram()
    {
        String diagramType = "graphviz";
        String outputType= "svg";
        String diagramContent = "digraph G {Hello->World}";

        InputStream diagramInputStream = mock(InputStream.class);

        when(this.krokiManager.renderDiagram(same(diagramType), same(outputType), same(diagramContent))).thenReturn(
            diagramInputStream);

        assertSame(diagramInputStream,
            this.krokiDiagramRenderer.render(diagramType, outputType, diagramContent));
    }

    private void mockNetwork(KrokiMacroConfiguration config)
    {
        when(this.containerManager.getIpAddress(this.containerId)).thenReturn(this.containerIpAddress);

        this.hostConfig = mock(HostConfig.class);
        when(this.containerManager.getHostConfig(config.getKrokiPort()))
            .thenReturn(this.hostConfig);
    }
}