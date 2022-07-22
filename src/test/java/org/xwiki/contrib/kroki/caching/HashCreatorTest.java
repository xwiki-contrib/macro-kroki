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
package org.xwiki.contrib.kroki.caching;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HashCreatorTest
{
    private final String originalText = "graphvizsvgdigraph G {Hello->World}";

    private final String md5Hash = "5cadb2ee0228ecf809a0995d855fd899";

    private final String emptyStringHash = "d41d8cd98f00b204e9800998ecf8427e";

    private HashCreator hashCreator;

    @BeforeAll
    public void setup()
    {
        hashCreator = new HashCreator();
    }

    @Test
    @DisplayName("Test MD5 hash generation empty input returns correct hash")
    public void testEmptyInput()
    {
        try {
            String emptyHash = hashCreator.createMD5Hash("");
            assertEquals(emptyStringHash, emptyHash);
        } catch (NoSuchAlgorithmException e) {
            fail("Error should not appear as the algorithm to be used is hard coded");
        }
    }

    @Test
    @DisplayName("Test MD5 hash generation with null input returns empty string")
    public void testNullInput()
    {
        try {
            String emptyHash = hashCreator.createMD5Hash(null);
            assertEquals("", emptyHash);
        } catch (NoSuchAlgorithmException e) {
            fail("Error should not appear as the algorithm to be used is hard coded");
        }
    }

    @Test
    @DisplayName("Test MD5 hash generation with known input")
    public void testKnowInput()
    {
        try {
            String actualHash = hashCreator.createMD5Hash(originalText);
            assertEquals(md5Hash, actualHash);
        } catch (NoSuchAlgorithmException e) {
            fail("Error should not appear as the algorithm to be used is hard coded");
        }
    }
}