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

import java.util.Collections;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.contrib.kroki.test.po.KrokiAdministrationSectionPage;
import org.xwiki.contrib.kroki.test.po.KrokiConfigurationEntryEditPage;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Kroki Configurations page.
 *
 * @version $Id$
 */
@UITest
class KrokiConfigurationPageIT
{
    @Test
    @Order(1)
    void administrationSection(TestUtils setup)
    {
        setup.loginAsSuperAdmin();
        AdministrationPage administrationPage = AdministrationPage.gotoPage();

        assertTrue(administrationPage.hasSection("Other", "Kroki Macro"));
        administrationPage.clickSection("Other", "Kroki Macro");

        KrokiAdministrationSectionPage krokiAdminSection = new KrokiAdministrationSectionPage();
        assertTrue(krokiAdminSection.getKrokiLiveTable().hasRow("Configuration Name", "Default"));
    }

    @Test
    @Order(2)
    void NewKrokiConfigurationTest(TestUtils setup) throws Exception
    {
        String newConfigPage = "Test Configuration";
        setup.rest().deletePage("XWiki.Kroki", newConfigPage);

        KrokiAdministrationSectionPage krokiAdminSection = KrokiAdministrationSectionPage.gotoPage();
        KrokiConfigurationEntryEditPage configurationPage = krokiAdminSection.addKrokiConfiguration(newConfigPage);

        configurationPage.setConfigurationName(newConfigPage);
        configurationPage.setDiagramTypes("test1, test2, test3");
        configurationPage.setDockerImage("hello-world");
        configurationPage.setContainerName("testContainer");
        configurationPage.setIsContainerReusable(false);
        configurationPage.setUseTLS(true);
        configurationPage.setHost("");
        configurationPage.setPort("8001");
        configurationPage.setHealthCheckBody("");
        configurationPage.setHealthCheckPath("");
        configurationPage.setHealthCheckHTTPVerb("GET");
        configurationPage.setHealthCheckAcceptedStatusCodes(Collections.singleton("200"));
        configurationPage.clickSaveAndView();

        krokiAdminSection = KrokiAdministrationSectionPage.gotoPage();
        assertTrue(krokiAdminSection.getKrokiLiveTable().hasRow("Configuration Name", newConfigPage));
    }
}
