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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * A manager for Kroki container configurations.
 *
 * @version $Id$
 */
@Component(roles = KrokiConfiguration.class)
@Singleton
public class KrokiConfiguration implements Initializable
{
    private static final String CONFIGURATION_RESOURCE_PATH = "container-config/";

    private static final String DIAGRAM_TYPES_FILE = "DiagramTypes.json";

    private static final String TEXT_AREA_START = "{{{";

    private static final String TEXT_AREA_END = "}}}";

    private static final String CONTAINER_NAME_PROP = "containerName";

    private static final String IS_CONTAINER_REUSABLE_PROP = "isContainerReusable";

    private static final String USE_TLS_PROP = "useTLS";

    private static final String HOST_PROP = "host";

    private static final String PORT_PROP = "port";

    private static final String XWIKI_CONFIG_CLASS = "XWiki.Kroki.ConfigClass";

    @Inject
    protected Provider<XWikiContext> xcontextProvider;

    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource configurationSource;

    @Inject
    private QueryManager queryManager;

    @Inject
    private Logger logger;

    /**
     * Mapping from diagram type to the name of the file/page containing the configuration for the used container.
     */
    private Map<String, String> diagramTypeToConfigFileMap;

    private Map<String, String> diagramTypeToConfigPageMap;

    /**
     * Mapping from configuration file/page to configuration object.
     */
    private Map<String, KrokiMacroConfiguration> fileToConfigurationMap;

    private Map<String, KrokiMacroConfiguration> pageToConfigurationMap;

    private Map<String, Timestamp> docNameToUpdateTimeMap;

    private ObjectMapper objectMapper;

    private DocumentReference configClassDocReference;

    @Override
    public void initialize() throws InitializationException
    {
        diagramTypeToConfigFileMap = new HashMap<>();
        diagramTypeToConfigPageMap = new HashMap<>();
        fileToConfigurationMap = new HashMap<>();
        pageToConfigurationMap = new HashMap<>();
        docNameToUpdateTimeMap = new HashMap<>();
        XWikiContext xcontext = xcontextProvider.get();
        try {
            configClassDocReference = xcontext.getWiki().getDocument(XWIKI_CONFIG_CLASS, EntityType.DOCUMENT, xcontext)
                .getDocumentReference();
        } catch (XWikiException e) {
            throw new RuntimeException(e);
        }
        initializeConfigurationsFromLocalFiles();
        initializeConfigurationsXWiki();
    }

    /**
     * @param diagramType the type of diagram to be rendered
     * @return the right container configuration for the diagram type
     */
    public KrokiMacroConfiguration getConfiguration(String diagramType)
    {
        initializeConfigurationsXWiki();
        String configurationName = diagramTypeToConfigPageMap.get(diagramType);

        if (configurationName != null) {
            return pageToConfigurationMap.get(configurationName);
        } else if (diagramTypeToConfigFileMap.get(diagramType) != null) {
            return fileToConfigurationMap.get(diagramTypeToConfigFileMap.get(diagramType));
        }

        return null;
    }

    /**
     * @return list of diagram types supported that can be generated
     */
    public List<String> getDiagramTypes()
    {
        Set<String> availableDiagramTypes = new HashSet<>(diagramTypeToConfigFileMap.keySet());
        availableDiagramTypes.addAll(diagramTypeToConfigPageMap.keySet());
        return new ArrayList<>(availableDiagramTypes);
    }

    private void initializeConfigurationsXWiki()
    {
        XWikiContext xcontext = xcontextProvider.get();
        List<BaseObject> objects = new ArrayList<>();
        List<BaseObject> foundObjects;
        XWikiDocument xdocument = null;

        String queryString = "select doc.fullName, doc.date from Document doc, BaseObject obj where doc"
            + ".fullName=obj.name and obj.className='XWiki.Kroki.ConfigClass'"
            + " and doc.fullName <> 'XWiki.Kroki.ConfigTemplate'";

        List<Object[]> docsAndDates = null;
        List<String> docNames = new ArrayList<>();

        try {
            Query configQuery = queryManager.createQuery(queryString, Query.XWQL);
            docsAndDates = configQuery.execute();

            for (Object[] nameDate : docsAndDates) {
                if (docNameToUpdateTimeMap.isEmpty() || !docNameToUpdateTimeMap.containsKey(nameDate[0])
                    || !docNameToUpdateTimeMap.get(nameDate[0]).equals(nameDate[1]))
                {
                    docNames =
                        docsAndDates.stream().map(nameAndDate -> (String) nameAndDate[0]).collect(Collectors.toList());
                    updateUpdateTimeMap(docsAndDates);
                    break;
                }
            }

            for (String documentName : docNames) {
                xdocument = xcontext.getWiki().getDocument(documentName, EntityType.DOCUMENT, xcontext);
                foundObjects = xdocument.getXObjects(configClassDocReference);
                objects.addAll(foundObjects);
            }
        } catch (QueryException | XWikiException e) {
            this.logger.debug("Could not initialize configuration from XWiki");
        }

        //if there are differences between the last and current configuration initialize from xwiki
        //else if no configurations are found from xwiki and the previous initialization was made from xwiki,
        //or if this is the first initialization and there are no Config Class objects, initialize the configurations
        // from the local config files
        //otherwise just keep the current configurations
        if (!objects.isEmpty()) {
            updateConfigsFromXWiki(objects);
        }
    }

    private void updateUpdateTimeMap(List<Object[]> docAndDateList)
    {
        docNameToUpdateTimeMap.clear();

        for (Object[] nameDate : docAndDateList) {
            docNameToUpdateTimeMap.put((String) nameDate[0], (Timestamp) nameDate[1]);
        }
    }

    private void initializeConfigurationsFromLocalFiles()
    {
        objectMapper = new ObjectMapper();

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
            String fileName = containerDiagramTypeEntry.getKey();
            try {
                initializeConfigurationFromLocalFiles(CONFIGURATION_RESOURCE_PATH, fileName, ".json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String diagramType : (List<String>) containerDiagramTypeEntry.getValue()) {
                diagramTypeToConfigFileMap.put(diagramType, fileName);
            }
        }
    }

    private void initializeConfigurationFromLocalFiles(String filePath, String fileName, String fileExetension)
        throws IOException
    {
        InputStream configFileStream =
            this.getClass().getClassLoader().getResourceAsStream(filePath + fileName + fileExetension);
        Map<String, Object> configurationParameters =
            objectMapper.readValue(configFileStream, new TypeReference<Map<String, Object>>()
            {
            });

        String image = (String) configurationParameters.get("image");
        String containerName = (String) configurationParameters.get(CONTAINER_NAME_PROP);
        boolean isContainerReusable = (boolean) configurationParameters.get(IS_CONTAINER_REUSABLE_PROP);
        boolean useTLS = (boolean) configurationParameters.get(USE_TLS_PROP);
        String host = (String) configurationParameters.get(HOST_PROP);
        Integer port =
            configurationParameters.get(PORT_PROP).equals("") ? null : (int) configurationParameters.get(PORT_PROP);
        Map<String, Object> healthCheckParameters =
            (Map<String, Object>) configurationParameters.get("healthCheckRequestParameters");
        HealthCheckRequestParameters healthCheckRequestParameters =
            new HealthCheckRequestParameters((String) healthCheckParameters.get("path"),
                (String) healthCheckParameters.get("body"), (String) healthCheckParameters.get("httpVerb"),
                (List<Integer>) healthCheckParameters.get("acceptedStatusCodes"));

        KrokiContainerConfiguration configuration = new KrokiContainerConfiguration.Builder()
            .setImage(image)
            .setContainerName(containerName)
            .setContainerReusable(isContainerReusable)
            .setTLS(useTLS)
            .setHost(host)
            .setPort(port)
            .setHealthCheckRequestParameters(healthCheckRequestParameters)
            .setConfigurationSource(configurationSource)
            .build();
            
        configuration.setConfigName(fileName);
        fileToConfigurationMap.put(fileName, configuration);
    }

    private void updateConfigsFromXWiki(List<BaseObject> objects)
    {
        diagramTypeToConfigPageMap.clear();
        pageToConfigurationMap.clear();

        for (BaseObject object : objects) {
            String configName = object.getStringValue("configurationName");
            String diagramTypes = object.getLargeStringValue("diagramTypes");
            String dockerImage = object.getStringValue("dockerImage");
            String containerName = object.getStringValue(CONTAINER_NAME_PROP);
            boolean isContainerReusable = object.getIntValue(IS_CONTAINER_REUSABLE_PROP) == 1;
            boolean useTLS = object.getIntValue(USE_TLS_PROP) == 1;
            String host = object.getStringValue(HOST_PROP);
            long configurationPort = object.getLongValue(PORT_PROP);
            List<String> healthCheckAcceptedStatusCodes = object.getListValue("healthCheckAcceptedStatusCodes");

            String healthCheckBody = object.getLargeStringValue("healthCheckBody");
            if (!StringUtils.isBlank(healthCheckBody) && healthCheckBody.startsWith(TEXT_AREA_START)
                && healthCheckBody.endsWith(TEXT_AREA_END))
            {
                healthCheckBody = healthCheckBody.substring(3, healthCheckBody.length() - 3);
            }

            String healthCheckPath = object.getStringValue("healthCheckPath");
            String healthCheckHTTPVerb = object.getStringValue("healthCheckHTTPVerb");

            String[] parsedDiagramTypes = {};

            if (!StringUtils.isBlank(diagramTypes)) {
                if (diagramTypes.startsWith(TEXT_AREA_START) && diagramTypes.endsWith(TEXT_AREA_END)) {
                    diagramTypes = diagramTypes.substring(3, diagramTypes.length() - 3);
                }
                parsedDiagramTypes = diagramTypes.split(", ");
            }

            List<Integer> parsedStatusCodes =
                healthCheckAcceptedStatusCodes.stream().map(Integer::parseInt).collect(Collectors.toList());
            HealthCheckRequestParameters healthCheckRequestParameters =
                new HealthCheckRequestParameters(healthCheckPath, healthCheckBody, healthCheckHTTPVerb,
                    parsedStatusCodes);

            Integer port = configurationPort == 0 ? null : (int) configurationPort;

            KrokiContainerConfiguration configuration = new KrokiContainerConfiguration.Builder()
                .setImage(dockerImage)
                .setContainerName(containerName)
                .setContainerReusable(isContainerReusable)
                .setTLS(useTLS)
                .setHost(host)
                .setPort(port)
                .setHealthCheckRequestParameters(healthCheckRequestParameters)
                .setConfigurationSource(configurationSource)
                .build();

            for (String diagramType : parsedDiagramTypes) {
                diagramTypeToConfigPageMap.put(diagramType, configName);
            }
            pageToConfigurationMap.put(configName, configuration);
        }
    }
}
