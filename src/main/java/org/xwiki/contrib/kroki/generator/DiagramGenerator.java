package org.xwiki.contrib.kroki.generator;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

import java.io.InputStream;

@Role
@Unstable
public interface DiagramGenerator {
    InputStream generateDiagram(String diagramLib, String imgFormat, String diagramContent);
}
