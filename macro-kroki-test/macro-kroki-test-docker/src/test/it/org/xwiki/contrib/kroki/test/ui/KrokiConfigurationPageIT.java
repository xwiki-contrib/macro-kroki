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
package org.xwiki.contrib.kroki.test.ui;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.contrib.kroki.test.po.KrokiConfigurationEntryEditPage;
import org.xwiki.contrib.kroki.test.po.KrokiHomePage;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.LiveTableElement;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Tests for Kroki Configurations page.
 *
 * @version $Id$
 */
@UITest
public class KrokiConfigurationPageIT
{
    @Test
    public void NewKrokiConfigurationTest(TestUtils setup) throws Exception
    {
        setup.loginAsSuperAdmin();

        String newConfigPage = "Test Configuration";

        setup.rest().deletePage("XWiki.Kroki", newConfigPage);

        KrokiHomePage homePage = KrokiHomePage.DEFAULT_KROKI_HOME_PAGE;

        KrokiConfigurationEntryEditPage configurationPage = homePage.addKrokiConfiguration(newConfigPage);

        configurationPage.setConfigurationName(newConfigPage);
        configurationPage.setDiagramTypes("test1, test2, test3");
        configurationPage.setDockerImage("hello-world");
        configurationPage.setContainerName("testContainer");
        configurationPage.setIsContainerReusable(false);
        configurationPage.setHost("");
        configurationPage.setPort("8001");
        configurationPage.setHealthCheckBody("");
        configurationPage.setHealthCheckPath("");
        configurationPage.setHealthCheckHTTPVerb("GET");
        Set<String> acceptedStatusCodes = new HashSet<>();
        acceptedStatusCodes.add("200");
        configurationPage.setHealthCheckAcceptedStatusCodes(acceptedStatusCodes);

        ViewPage vp = configurationPage.clickSaveAndView();

        vp.clickBreadcrumbLink("Kroki Configurations");

        LiveTableElement lt = homePage.getKrokiLiveTable();
        Assert.assertTrue(lt.hasRow("Configuration Name", newConfigPage));
    }

    @Test
    public void KrokiConfigurationsInAdministrationSection(TestUtils setup, XWikiWebDriver driver)
    {
        setup.loginAsSuperAdmin();

        AdministrationPage administrationPage = AdministrationPage.gotoPage();

        Assert.assertTrue(administrationPage.hasSection("Other", "Kroki Configuration"));

        administrationPage.clickSection("Other", "Kroki Configuration");

        LiveTableElement lt = new LiveTableElement("kroki");
        lt.waitUntilReady();
        Assert.assertTrue(lt.hasRow("Configuration Name", "Default"));
        WebElement createConfigButton = driver.findElementByName("createConfigBtn");
        Assert.assertNotNull(createConfigButton);
    }
}
