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
package org.xwiki.contrib.kroki.internal.caching;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheException;
import org.xwiki.cache.CacheManager;
import org.xwiki.cache.config.CacheConfiguration;
import org.xwiki.cache.eviction.LRUEvictionConfiguration;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.resource.temporary.TemporaryResourceReference;

/**
 * A cache manager for managing TemporaryResourceReferences to generated diagrams.
 *
 * @version $Id$
 */
@Component(roles = DiagramCacheManager.class)
@Singleton
public class DiagramCacheManager implements Initializable
{
    //one week = 604800 seconds
    private static final int MAX_IDLE = 604800;

    private static final int LIFE_SPAN = 604800;

    private static final String CACHE_ID = "xwiki.store.kroki-cache-" + System.currentTimeMillis();

    @Inject
    private Logger logger;

    @Inject
    private CacheManager cacheManager;

    private Cache<TemporaryResourceReference> cache;

    @Override
    public void initialize() throws InitializationException
    {
        this.logger.debug("Initializing the CacheManager.");
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setConfigurationId(CACHE_ID);

        LRUEvictionConfiguration lru = new LRUEvictionConfiguration(1000);
        lru.setMaxIdle(MAX_IDLE);
        lru.setLifespan(LIFE_SPAN);

        cacheConfiguration.put(LRUEvictionConfiguration.CONFIGURATIONID, lru);

        try {
            cache = cacheManager.createNewLocalCache(cacheConfiguration);
        } catch (CacheException e) {
            throw new RuntimeException(e);
        }

        this.logger.debug("CacheManager initialized");
    }

    /**
     * Adds a new resource to the cache.
     *
     * @param contentHash the key value representing a hash
     * @param resourceReference a temporary resource reference to a previously generated diagram
     */
    public void addResourceToCache(String contentHash, TemporaryResourceReference resourceReference)
    {
        cache.set(contentHash, resourceReference);
    }

    /**
     * Gets a resource stored in the cache based on it's associated hash value.
     *
     * @param contentHash the key value representing a hash
     * @return the resource reference mapped to the contentHash or null if there is no associated resource reference
     */
    public TemporaryResourceReference getResourceFromCache(String contentHash)
    {
        return cache.get(contentHash);
    }
}
