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

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.po.InlinePage;

/**
 * Represents a Kroki Configuration page being added.
 *
 * @version $Id$
 */
public class KrokiConfigurationEntryEditPage extends InlinePage
{
    @FindBy(id = "XWiki.Kroki.ConfigClass_0_configurationName")
    private WebElement configurationNameElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_diagramTypes")
    private WebElement diagramTypesElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_dockerImage")
    private WebElement dockerImageElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_containerName")
    private WebElement containerNameElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_isContainerReusable")
    private WebElement isContainerReusableElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_host")
    private WebElement hostElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_port")
    private WebElement portElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_healthCheckPath")
    private WebElement healthPathElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_healthCheckBody")
    private WebElement healthBodyElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_healthCheckHTTPVerb")
    private WebElement healthVerbElement;

    @FindBy(id = "XWiki.Kroki.ConfigClass_0_configurationName")
    private WebElement healthStatusCodesElement;

    /**
     * @param configurationName the name of the configuration
     */
    public void setConfigurationName(String configurationName)
    {
        this.configurationNameElement.clear();
        this.configurationNameElement.sendKeys(configurationName);
    }

    /**
     * @param diagramTypes the name of the configuration
     */
    public void setDiagramTypes(String diagramTypes)
    {
        this.diagramTypesElement.clear();
        this.diagramTypesElement.sendKeys(diagramTypes);
    }

    /**
     * @param dockerImage the name of the docker image to pull
     */
    public void setDockerImage(String dockerImage)
    {
        this.dockerImageElement.clear();
        this.dockerImageElement.sendKeys(dockerImage);
    }

    /**
     * @param containerName the name of the docker container
     */
    public void setContainerName(String containerName)
    {
        this.containerNameElement.clear();
        this.containerNameElement.sendKeys(containerName);
    }

    /**
     * @param isContainerReusable true if container can be reused if it exists
     */
    public void setIsContainerReusable(boolean isContainerReusable)
    {
        isContainerReusableElement.click();
        if (isContainerReusable) {
            isContainerReusableElement.findElement(By.xpath("//option[@label='Yes']"));
        } else {
            isContainerReusableElement.findElement(By.xpath("//option[@label='No']"));
        }
    }

    /**
     * @param host the host used to send requests
     */
    public void setHost(String host)
    {
        this.hostElement.clear();
        this.hostElement.sendKeys(host);
    }

    /**
     * @param port the port used to send requests
     */
    public void setPort(String port)
    {
        this.portElement.clear();
        this.portElement.sendKeys(port);
    }

    /**
     * @param healthCheckPath path used for health request
     */
    public void setHealthCheckPath(String healthCheckPath)
    {
        this.healthPathElement.clear();
        this.healthPathElement.sendKeys(healthCheckPath);
    }

    /**
     * @param healthCheckBody body used for health request
     */
    public void setHealthCheckBody(String healthCheckBody)
    {
        this.healthBodyElement.clear();
        this.healthBodyElement.sendKeys(healthCheckBody);
    }

    /**
     * @param healthCheckHTTPVerb http verb used for health request
     */
    public void setHealthCheckHTTPVerb(String healthCheckHTTPVerb)
    {
        this.healthVerbElement.clear();
        this.healthVerbElement.sendKeys(healthCheckHTTPVerb);
    }

    /**
     * @param healthCheckAcceptedStatusCodes status codes accepted as response from health check
     */
    public void setHealthCheckAcceptedStatusCodes(Set<String> healthCheckAcceptedStatusCodes)
    {
        for (String statusCode : healthCheckAcceptedStatusCodes) {
            if (statusCode.equals("200")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-0"))
                    .click();
            }
            if (statusCode.equals("201")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-1"))
                    .click();
            }
            if (statusCode.equals("202")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-2"))
                    .click();
            }
            if (statusCode.equals("203")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-3"))
                    .click();
            }
            if (statusCode.equals("204")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-4"))
                    .click();
            }
            if (statusCode.equals("205")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-5"))
                    .click();
            }
            if (statusCode.equals("206")) {
                this.healthStatusCodesElement.findElement(By.id("xwiki-form-healthCheckAcceptedStatusCodes-0-6"))
                    .click();
            }
        }
    }
}
