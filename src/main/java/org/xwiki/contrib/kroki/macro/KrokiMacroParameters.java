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
import org.xwiki.properties.annotation.PropertyDisplayType;
import org.xwiki.properties.annotation.PropertyMandatory;
import org.xwiki.properties.annotation.PropertyName;

/**
 * Parameters for the KrokiMacro.
 *
 * @version $Id$
 */
public class KrokiMacroParameters
{
    /**
     * The type used to associate a picker to the {@code diagramType} parameter.
     *
     * @see KrokiMacroParameters#setDiagramType(String)
     */
    public static class DiagramType
    {
    }

    private String diagramType;

    private String outputType = "svg";

    /**
     * @return the type of diagram
     */
    public String getDiagramType()
    {
        return diagramType;
    }

    /**
     * @param diagramType the type of diagram to be set
     */
    @PropertyMandatory
    @PropertyName("Diagram Type")
    @PropertyDescription("The diagram type to be rendered")
    @PropertyDisplayType(DiagramType.class)
    public void setDiagramType(String diagramType)
    {
        this.diagramType = diagramType;
    }

    /**
     * @return the type of output file to be rendered
     */
    public String getOutputType()
    {
        return outputType;
    }

    /**
     * @param outputType sets the type of file rendered
     */
    @PropertyName("Output Type")
    @PropertyDescription("The file format for the returned diagram")
    @PropertyDisplayType(OutputType.class)
    public void setOutputType(String outputType)
    {
        this.outputType = outputType;
    }
}
