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
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.test.ui.po.LiveTableElement;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Represents actions that can be done on Kroki home page.
 *
 * @version $Id$
 */
public class KrokiHomePage extends ViewPage
{
    /**
     * Main wiki id.
     */
    public static final String MAIN_WIKI = "xwiki";

    /**
     * Kroki home page document reference.
     */
    public static final KrokiHomePage DEFAULT_KROKI_HOME_PAGE =
        new KrokiHomePage(new DocumentReference(MAIN_WIKI, Arrays.asList("XWiki", "Kroki"), "WebHome"));

    private final EntityReference homeReference;

    /**
     * @param homeReference the reference to the home page where Kroki Configurations can be added
     */
    public KrokiHomePage(EntityReference homeReference)
    {
        this.homeReference = homeReference;
    }

    /**
     * Opens the home page.
     */
    public void gotoPage()
    {
        getUtil().gotoPage(this.homeReference);
    }

    /**
     * @return the String reference to the space Kroki Configurations page(e.g. "{@code Space1.Space2})
     */
    public String getSpaces()
    {
        return getUtil().serializeReference(
            this.homeReference.extractReference(EntityType.SPACE).removeParent(new WikiReference(MAIN_WIKI)));
    }

    /**
     * @return the name of the home page where the Kroki Configurations page is installed (e.g. "{@code WebHome})
     */
    public String getPage()
    {
        return this.homeReference.getName();
    }

    /**
     * @param configName the name of the configuration page to add
     * @return the new FAQ entry page
     */
    public KrokiConfigurationEntryEditPage addKrokiConfiguration(String configName)
    {
        WebElement configNameField = getDriver().findElementByName("docName");
        WebElement createConfigButton = getDriver().findElementByName("createConfigBtn");
        configNameField.clear();
        configNameField.sendKeys(configName);
        createConfigButton.click();
        return new KrokiConfigurationEntryEditPage();
    }

    /**
     * @return the livetable element of the Kroki Configurations page
     */
    public LiveTableElement getKrokiLiveTable()
    {
        LiveTableElement lt = new LiveTableElement("kroki");
        lt.waitUntilReady();
        return lt;
    }
}
