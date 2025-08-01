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
package org.xwiki.contrib.kroki.internal.macro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.contrib.kroki.internal.caching.DiagramCacheManager;
import org.xwiki.contrib.kroki.macro.KrokiMacroParameters;
import org.xwiki.contrib.kroki.renderer.DiagramRenderer;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.match.BlockMatcher;
import org.xwiki.rendering.internal.renderer.event.EventBlockRenderer;
import org.xwiki.rendering.internal.renderer.event.EventRenderer;
import org.xwiki.rendering.internal.renderer.event.EventRendererFactory;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.renderer.BlockRenderer;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.resource.ResourceReferenceSerializer;
import org.xwiki.resource.SerializeResourceReferenceException;
import org.xwiki.resource.UnsupportedResourceReferenceException;
import org.xwiki.resource.temporary.TemporaryResourceReference;
import org.xwiki.resource.temporary.TemporaryResourceStore;
import org.xwiki.test.annotation.BeforeComponent;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectComponentManager;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;
import org.xwiki.test.mockito.MockitoComponentManager;
import org.xwiki.url.ExtendedURL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ComponentTest
@ComponentList({ EventBlockRenderer.class, EventRendererFactory.class, EventRenderer.class })
class KrokiMacroTest
{
    @InjectComponentManager
    private MockitoComponentManager componentManager;

    @InjectMockComponents
    private KrokiMacro krokiMacro;

    @MockComponent
    private TemporaryResourceStore temporaryResourceStore;

    @MockComponent
    @Named("standard/tmp")
    private ResourceReferenceSerializer<TemporaryResourceReference, ExtendedURL> resourceReferenceSerializer;

    @MockComponent
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @MockComponent
    private DiagramRenderer diagramRenderer;

    @MockComponent
    private DiagramCacheManager cacheManager;

    private MacroTransformationContext context;

    private BlockRenderer eventRenderer;

    private final KrokiMacroParameters parameters = new KrokiMacroParameters();

    private final String content = "content";

    private TemporaryResourceReference resourceReference;

    private DocumentReference docReference;

    private ResourceReference fileReference;

    private InputStream contentStream;

    private final String hash = "53f3f0e5430f905f2a6d7f8ca51870d0";

    @BeforeComponent
    void configure() throws SerializeResourceReferenceException, UnsupportedResourceReferenceException
    {
        parameters.setDiagramType("graphviz");
        parameters.setOutputType("svg");
        contentStream = mock(InputStream.class);

        when(diagramRenderer.render(parameters.getDiagramType(), parameters.getOutputType(), content)).thenReturn(
            contentStream);

        docReference = mock(DocumentReference.class);
        when(this.documentReferenceResolver.resolve(any(String.class))).thenReturn(docReference);

        resourceReference = new TemporaryResourceReference("kroki",
            Arrays.asList(parameters.getDiagramType(), UUID.randomUUID() + "." + parameters.getOutputType()),
            docReference);

        ExtendedURL extendedURL = mock(ExtendedURL.class);
        when(extendedURL.serialize()).thenReturn("graphviz/svg");
        when(this.resourceReferenceSerializer.serialize(any(TemporaryResourceReference.class))).thenReturn(extendedURL);

        MacroBlock block = mock(MacroBlock.class);
        when(block.getFirstBlock(any(BlockMatcher.class),any(Block.Axes.class))).thenReturn(null);

        context = mock(MacroTransformationContext.class);
        when(context.getCurrentMacroBlock()).thenReturn(block);
    }


    @Test
    void executeWithReferenceFromCacheTest()
        throws IOException, MacroExecutionException, ComponentLookupException
    {
        when(cacheManager.getResourceFromCache(hash)).thenReturn(resourceReference);

        List<Block> output = this.krokiMacro.execute(parameters, content, context);

        verify(this.documentReferenceResolver).resolve(nullable(String.class));
        verify(this.cacheManager).getResourceFromCache(hash);
        verify(this.temporaryResourceStore, never()).createTemporaryFile(any(TemporaryResourceReference.class),
            any(InputStream.class));
        verify(this.diagramRenderer, never()).render(any(String.class), any(String.class), any(String.class));
        verify(this.cacheManager, never()).addResourceToCache(any(String.class), any(TemporaryResourceReference.class));

        List<String> events = Arrays.asList(
            "beginParagraph",
            "beginLink [Typed = [true] Type = [url] Reference = [graphviz/svg]] [true] "
                + "[[target]=[_blank][title]=[graphviz diagram]]",
            "onImage [Typed = [true] Type = [url] Reference = [graphviz/svg]] [true] [[alt]=[graphviz diagram]]",
            "endLink [Typed = [true] Type = [url] Reference = [graphviz/svg]] [true] [[target]=[_blank][title]=[graphviz "
                + "diagram]]",
            "endParagraph",
            ""
        );

        assertBlockEvents(StringUtils.join(events, "\n"), output.get(0));
    }

    @Test
    void executeWithNewReferenceTest() throws MacroExecutionException, IOException
    {
        when(cacheManager.getResourceFromCache(any(String.class))).thenReturn(null);

        List<Block> output = this.krokiMacro.execute(parameters, content, context);
        verify(this.documentReferenceResolver).resolve(nullable(String.class));
        verify(this.cacheManager).getResourceFromCache(hash);
        verify(diagramRenderer).render(parameters.getDiagramType(), parameters.getOutputType(), content);
        verify(this.temporaryResourceStore).createTemporaryFile(any(TemporaryResourceReference.class), eq(contentStream));
        verify(this.cacheManager).addResourceToCache(eq(hash), any(TemporaryResourceReference.class));
    }

    private void assertBlockEvents(String expected, Block block) throws ComponentLookupException
    {
        DefaultWikiPrinter printer = new DefaultWikiPrinter();
        eventRenderer = componentManager.getInstance(BlockRenderer.class, "event/1.0");
        eventRenderer.render(block, printer);

        assertEquals(expected, printer.toString());
    }
}