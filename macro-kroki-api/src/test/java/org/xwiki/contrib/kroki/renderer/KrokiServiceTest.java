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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xwiki.contrib.kroki.internal.rendrer.KrokiService;
import org.xwiki.test.annotation.BeforeComponent;
import org.xwiki.test.junit5.mockito.ComponentTest;

import static org.junit.jupiter.api.Assertions.*;

@ComponentTest
public class KrokiServiceTest
{
    private KrokiService krokiService;

    @BeforeComponent
    public void initialize(){
        krokiService = new KrokiService();
    }


    @Test
    @DisplayName("Test creation of a valid path")
    public void createRequestPathValidInputTest()
    {
        String actual = krokiService.createRequestPath("http://", "test", "8000", "graphviz", "svg");
        assertEquals("http://test:8000/graphviz/svg", actual);
    }

    @Test
    @DisplayName("Test creation of path with null components")
    public void createRequestPathNullInputTest()
    {
        String actual = krokiService.createRequestPath(null, null, null, null, null);
        assertEquals("nullnull:null/null/null", actual);
    }

    @Test
    @DisplayName("Test creation of path with empty components")
    public void createRequestPathEmptyInputTest()
    {
        String actual = krokiService.createRequestPath("", "", "", "", "");
        assertEquals("://", actual);
    }
}