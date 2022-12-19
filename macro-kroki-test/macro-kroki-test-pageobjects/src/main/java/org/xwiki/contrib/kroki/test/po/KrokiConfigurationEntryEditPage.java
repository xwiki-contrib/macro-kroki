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
package org.xwiki.contrib.kroki.test.po;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.po.InlinePage;

/**
 * Represents a Kroki Configuration page being added.
 *
 * @version $Id$
 */
public class KrokiConfigurationEntryEditPage extends InlinePage
{
    /**
     * @param configurationName the name of the configuration
     */
    public void setConfigurationName(String configurationName)
    {
        setValue("configurationName", configurationName);
    }

    /**
     * @param diagramTypes the name of the configuration
     */
    public void setDiagramTypes(String diagramTypes)
    {
        setValue("diagramTypes", diagramTypes);
    }

    /**
     * @param dockerImage the name of the docker image to pull
     */
    public void setDockerImage(String dockerImage)
    {
        setValue("dockerImage", dockerImage);
    }

    /**
     * @param containerName the name of the docker container
     */
    public void setContainerName(String containerName)
    {
        setValue("containerName", containerName);
    }

    /**
     * @param isContainerReusable true if container can be reused if it exists
     */
    public void setIsContainerReusable(boolean isContainerReusable)
    {
        setValue("isContainerReusable", isContainerReusable ? "1" : "0");
    }

    /**
     * @param useTLS whether to use a secure connection or not
     */
    public void setUseTLS(boolean useTLS)
    {
        setValue("useTLS", useTLS ? "1" : "0");
    }

    /**
     * @param host the host used to send requests
     */
    public void setHost(String host)
    {
        setValue("host", host);
    }

    /**
     * @param port the port used to send requests
     */
    public void setPort(String port)
    {
        setValue("port", port);
    }

    /**
     * @param healthCheckPath path used for health request
     */
    public void setHealthCheckPath(String healthCheckPath)
    {
        setValue("healthCheckPath", healthCheckPath);
    }

    /**
     * @param healthCheckBody body used for health request
     */
    public void setHealthCheckBody(String healthCheckBody)
    {
        setValue("healthCheckBody", healthCheckBody);
    }

    /**
     * @param healthCheckHTTPVerb http verb used for health request
     */
    public void setHealthCheckHTTPVerb(String healthCheckHTTPVerb)
    {
        setValue("healthCheckHTTPVerb", healthCheckHTTPVerb);
    }

    /**
     * @param healthCheckAcceptedStatusCodes status codes accepted as response from health check
     */
    public void setHealthCheckAcceptedStatusCodes(Set<String> healthCheckAcceptedStatusCodes)
    {
        List<WebElement> checkboxes =
            getDriver().findElementsWithoutWaiting(By.name("XWiki.Kroki.ConfigClass_0_healthCheckAcceptedStatusCodes"));

        // Make sure all check boxes are visible on the page by scrolling to the last check box.
        checkboxes.stream().filter(WebElement::isDisplayed).reduce((first, second) -> second).map(lastCheckbox -> {
            getDriver().scrollTo(lastCheckbox);
            getDriver().createActions().moveToElement(lastCheckbox).perform();
            return lastCheckbox;
        });

        // Clear the current selection.
        checkboxes.stream().filter(WebElement::isSelected).forEach(WebElement::click);

        // Select the specified status codes.
        checkboxes.stream().filter(WebElement::isDisplayed)
            .filter(checkbox -> healthCheckAcceptedStatusCodes.contains(checkbox.getAttribute("value")))
            .forEach(WebElement::click);
    }
}
