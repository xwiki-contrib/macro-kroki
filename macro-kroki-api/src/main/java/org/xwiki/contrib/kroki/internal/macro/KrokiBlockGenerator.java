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

import java.util.Collections;

import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.ImageBlock;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.listener.reference.ResourceReference;

/**
 * Used to generate the result block for the Kroki Macro.
 *
 * @version $Id$
 */
public final class KrokiBlockGenerator
{
    private KrokiBlockGenerator()
    {
    }

    /**
     * Generate a block for the generated diagram file.
     *
     * @param fileReference reference to the generated diagram
     * @param fileName the title for the link block
     * @return block containing an image and link to image
     */
    public static Block createImageRefBlock(ResourceReference fileReference, String fileName)
    {
        ImageBlock img = new ImageBlock(fileReference, true);
        img.setParameter("alt", fileName);
        LinkBlock linkBlock = new LinkBlock(Collections.singletonList(img), fileReference, true);
        linkBlock.setParameter("title", fileName);
        linkBlock.setParameter("target", "_blank");

        return new ParagraphBlock(Collections.singletonList(linkBlock));
    }
}
