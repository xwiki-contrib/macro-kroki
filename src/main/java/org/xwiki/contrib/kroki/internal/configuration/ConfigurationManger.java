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

package org.xwiki.contrib.kroki.internal.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A manager for Kroki container configurations.
 *
 * @version $Id$
 */
@Component(roles = ConfigurationManger.class)
@Singleton
public class ConfigurationManger implements Initializable
{
    private static final String CONFIGURATION_RESOURCE_PATH = "container-config/";

    private static final String DIAGRAM_TYPES_FILE = "DiagramTypes.json";

    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource configurationSource;

    /**
     * Mapping from diagram type to the name of the file containing the configuration for the responsible container.
     */
    private Map<String, String> diagramTypeToConfigFileMap;

    /**
     * Mapping from configuration file to configuration object.
     */
    private Map<String, KrokiMacroConfiguration> fileToConfigurationMap;

    private ObjectMapper objectMapper;

    @Override
    public void initialize() throws InitializationException
    {
        objectMapper = new ObjectMapper();

        diagramTypeToConfigFileMap = new HashMap<>();
        fileToConfigurationMap = new HashMap<>();

        Map<String, Object> diagramContainerToType = null;
        //Get all the diagram types supported and their configuration file names
        try {
            InputStream diagramTypes =
                this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_RESOURCE_PATH + DIAGRAM_TYPES_FILE);
            diagramContainerToType = objectMapper.readValue(diagramTypes, new TypeReference<Map<String, Object>>()
            {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Object> containerDiagramTypeEntry : diagramContainerToType.entrySet()) {
            fileToConfigurationMap.put(containerDiagramTypeEntry.getKey(), null);
            for (String diagramType : (List<String>) containerDiagramTypeEntry.getValue()) {
                diagramTypeToConfigFileMap.put(diagramType, containerDiagramTypeEntry.getKey());
            }
        }
    }

    /**
     * @param diagramType the type of diagram to be rendered
     * @return the right container configuration for the diagram type
     */
    public KrokiMacroConfiguration getConfiguration(String diagramType)
    {
        String configurationFileName = diagramTypeToConfigFileMap.get(diagramType);
        if (configurationFileName == null) {
            return null;
        }

        KrokiMacroConfiguration configuration = fileToConfigurationMap.get(configurationFileName);

        if (configuration == null) {
            try {
                InputStream configFileStream = this.getClass().getClassLoader()
                    .getResourceAsStream(CONFIGURATION_RESOURCE_PATH + configurationFileName + ".json");
                Map<String, Object> configurationParameters =
                    objectMapper.readValue(configFileStream, new TypeReference<Map<String, Object>>()
                    {
                    });
                configuration = new JsonKrokiMacroConfiguration(configurationFileName, configurationParameters,
                    configurationSource);
                fileToConfigurationMap.put(configurationFileName, configuration);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            configuration = fileToConfigurationMap.get(configurationFileName);
        }

        return configuration;
    }

    /**
     * @return list of diagram types supported that can be generated
     */
    public List<String> getDiagramTypes()
    {
        return new ArrayList<>(diagramTypeToConfigFileMap.keySet());
    }
}
