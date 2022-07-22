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
package org.xwiki.contrib.kroki.caching;

import org.junit.jupiter.api.Test;
import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheException;
import org.xwiki.cache.CacheManager;
import org.xwiki.cache.config.CacheConfiguration;
import org.xwiki.resource.temporary.TemporaryResourceReference;
import org.xwiki.test.annotation.BeforeComponent;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ComponentTest
class DiagramCacheManagerTest
{
    @InjectMockComponents
    private DiagramCacheManager diagramCacheManager;

    @MockComponent
    private CacheManager cacheManager;

    private Cache<TemporaryResourceReference> cache;

    private TemporaryResourceReference temp1;

    private TemporaryResourceReference temp2;

    @BeforeComponent
    @SuppressWarnings("unchecked")
    void configure() throws CacheException
    {
        temp1 = mock(TemporaryResourceReference.class);

        cache = (Cache<TemporaryResourceReference>) mock(Cache.class);

        when(cache.get("temp1")).thenReturn(temp1);

        doReturn(cache).when(this.cacheManager).createNewLocalCache(any(CacheConfiguration.class));

        doAnswer(I -> this.temp2 = I.getArgument(1)).when(this.cache).set(any(String.class),
            any(TemporaryResourceReference.class));
    }

    @Test
    void initializationTest() throws CacheException
    {
        verify(this.cacheManager).createNewLocalCache(any(CacheConfiguration.class));
    }

    @Test
    void getResourceFromCache()
    {
        assertEquals(temp1, diagramCacheManager.getResourceFromCache("temp1"));
        verify(this.cache).get("temp1");
    }

    @Test
    void addResourceToCache()
    {
        TemporaryResourceReference temp = mock(TemporaryResourceReference.class);
        diagramCacheManager.addResourceToCache("temp", temp);

        verify(this.cache).set(eq("temp"), any(TemporaryResourceReference.class));
        assertEquals(temp, temp2);
    }
}