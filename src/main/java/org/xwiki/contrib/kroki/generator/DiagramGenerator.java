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

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

/**
 * Generic interface for a text diagram generator.
 *
 * @version $Id$
 * @since 13.10.7
 */
@Role
@Unstable
public interface DiagramGenerator
{
    /**
     * Creates an image from it's textual description.
     *
     * @param diagramLib diagram libaraey to be used
     * @param imgFormat the format of the returned image
     * @param diagramContent the text content to be transformed
     * @return the image's input stream
     */
    InputStream generateDiagram(String diagramLib, String imgFormat, String diagramContent);
}
