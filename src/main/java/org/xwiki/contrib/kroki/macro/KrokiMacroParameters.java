package org.xwiki.contrib.kroki.macro;

import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;

/**
 * Parameters for the {@link org.xwiki.contrib.kroki.macro.KrokiMacro Macro.
 */
public class KrokiMacroParameters {
    private String diagramLib;
    private String imgFormat;

    /**
     * @return the type of diagram
     */
    public String getDiagramLib() {
        return diagramLib;
    }

    /**
     * @return the diagram's image format
     */
    public String getImgFormat() {
        return imgFormat;
    }

    @PropertyMandatory
    @PropertyDescription("The image format for the returned graph")
    public void setImgFormat(String imgFormat) {
        this.imgFormat = imgFormat;
    }

    @PropertyMandatory
    @PropertyDescription("The diagram library used by Kroki to generate the diagram")
    public void setDiagramLib(String diagramLib) {
        this.diagramLib = diagramLib;
    }
}