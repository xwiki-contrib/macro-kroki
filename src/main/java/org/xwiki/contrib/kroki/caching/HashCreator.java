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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Generates hash codes.
 *
 * @version $Id$
 */
public class HashCreator
{
    /**
     * Generates the hash for a string using the MD5 algorithm.
     *
     * @param input the string whose hash will be generated
     * @return the generated hash
     * @throws NoSuchAlgorithmException if instance of the MD5 algorithm can't be obtained
     */
    public String createMD5Hash(final String input) throws NoSuchAlgorithmException
    {
        if (input == null) {
            return "";
        }

        String hashtext = null;
        MessageDigest md = MessageDigest.getInstance("MD5");

        // Compute message digest of the input
        byte[] messageDigest = md.digest(input.getBytes());

        hashtext = convertToHex(messageDigest);

        return hashtext;
    }

    private String convertToHex(final byte[] messageDigest)
    {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
