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

import java.util.Arrays;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.administration.test.po.AdministrationSectionPage;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.test.ui.po.LiveTableElement;

/**
 * Represents actions that can be done on Kroki home page.
 *
 * @version $Id$
 */
public class KrokiAdministrationSectionPage extends AdministrationSectionPage
{
    @FindBy(name = "docName")
    private WebElement configNameField;

    @FindBy(name = "createConfigBtn")
    private WebElement createConfigButton;

    private LiveTableElement liveTable = new LiveTableElement("krokiConfigPages");

    /**
     * Default constructor.
     */
    public KrokiAdministrationSectionPage()
    {
        super("kroki");
        this.liveTable.waitUntilReady();
    }

    /**
     * Opens the Kroki administration section page.
     * 
     * @return the Kroki administration section page
     */
    public static KrokiAdministrationSectionPage gotoPage()
    {
        getUtil().gotoPage(new LocalDocumentReference(Arrays.asList("XWiki", "Kroki"), "WebHome"));
        return new KrokiAdministrationSectionPage();
    }

    /**
     * @param configName the name of the configuration page to add
     * @return the edit page for the new kroki configuration
     */
    public KrokiConfigurationEntryEditPage addKrokiConfiguration(String configName)
    {
        configNameField.clear();
        configNameField.sendKeys(configName);
        createConfigButton.click();
        return new KrokiConfigurationEntryEditPage();
    }

    /**
     * @return the live table element listing available Kroki configurations
     */
    public LiveTableElement getKrokiLiveTable()
    {
        return liveTable;
    }
}
