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
package org.xwiki.contrib.kroki.configuration;

import java.util.Arrays;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Diagram generator configuration options for BPMN container.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named("bpmn-config")
public class BpmnDiagramGeneratorConfiguration extends DefaultDiagramGeneratorConfiguration
{
    @Override
    public String getKrokiDockerImage()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBpmnDockerImage", "yuzutech/kroki-bpmn");
    }

    @Override
    public String getKrokiDockerContainerName()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBpmnDockerContainerName", "kroki-bpmn-container");
    }

    @Override
    public boolean isKrokiDockerContainerReusable()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBpmnDockerContainerReusable", true);
    }

    @Override
    public int getKrokiRemoteDebuggingPort()
    {
        return this.configurationSource.getProperty(PREFIX + "krokiBpmnRemoteDebuggingPort", 8003);
    }

    @Override
    public HealthCheckRequestParameters getHealthCheckRequest()
    {
        return new HealthCheckRequestParameters("/bpmn/svg",
            "<bpmn:definitions id=\"definitions_2\" targetNamespace=\"http://www.bpmn-sketch-miner.ai\""
                + " exporter=\"BPMN Sketch Miner\" exporterVersion=\"1.17.6.3337\">\n"
                + "<bpmn:collaboration id=\"collaboration_3\">\n"
                + "<bpmn:participant id=\"participant_7\" name=\"\" processRef=\"process_6\"/>\n"
                + "</bpmn:collaboration>\n" + "<bpmn:process id=\"process_6\" isExecutable=\"false\">\n"
                + "<bpmn:laneSet id=\"laneSet_8\">\n" + "</bpmn:laneSet>\n" + "</bpmn:process>\n"
                + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_4\">\n"
                + "<bpmndi:BPMNPlane id=\"BPMNPlane_5\" bpmnElement=\"collaboration_3\">\n"
                + "<bpmndi:BPMNShape id=\"participant_7_di\" "
                + " bpmnElement=\"participant_7\" isHorizontal=\"false\">\n"
                + "<dc:Bounds x=\"0\" y=\"40\" width=\"132\" height=\"199\"/>\n" + "</bpmndi:BPMNShape>\n"
                + "</bpmndi:BPMNPlane>\n" + "</bpmndi:BPMNDiagram>\n" + "</bpmn:definitions>", "POST",
            Arrays.asList(200, 201));
    }
}
