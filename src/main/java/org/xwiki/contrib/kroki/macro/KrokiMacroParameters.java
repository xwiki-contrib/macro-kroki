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
package org.xwiki.contrib.kroki.macro;

import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;

/**
 * Parameters for the {@link org.xwiki.contrib.kroki.macro.KrokiMacro Macro.
 *
 * @version $Id$
 */
public class KrokiMacroParameters
{
    private String diagramLib;

    private String imgFormat;

    /**
     * @return the type of diagram
     */
    public String getDiagramLib()
    {
        return diagramLib;
    }

    /**
     * Sets the library diagram.
     *
     * @param diagramLib the format of the image
     */
    @PropertyMandatory
    @PropertyDescription("The diagram library used by Kroki to generate the diagram")
    public void setDiagramLib(String diagramLib)
    {
        this.diagramLib = diagramLib;
    }

    /**
     * @return the diagram's image format
     */
    public String getImgFormat()
    {
        return imgFormat;
    }

    /**
     * Sets the format of the image.
     *
     * @param imgFormat the format of the image
     */
    @PropertyMandatory
    @PropertyDescription("The image format for the returned graph")
    public void setImgFormat(String imgFormat)
    {
        this.imgFormat = imgFormat;
    }
}
