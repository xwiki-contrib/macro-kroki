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
package org.xwiki.contrib.kroki.utils;

import java.util.List;

/**
 * Class used to store paramaters for testing the availability of a service.
 *
 * @version $Id$
 */
public class HealthCheckRequestParameters
{
    private String path;

    private String body;

    private String httpVerb;

    private List<Integer> acceptedStatusCodes;

    /**
     * Initialize the parameters for a health check.
     *
     * @param path the request path
     * @param body the body of the request
     * @param httpVerb the http action used for the request
     * @param acceptedStatusCodes the status codes for which the service is considered healthy
     */
    public HealthCheckRequestParameters(String path, String body, String httpVerb, List<Integer> acceptedStatusCodes)
    {
        this.path = path;
        this.body = body;
        this.httpVerb = httpVerb;
        this.acceptedStatusCodes = acceptedStatusCodes;
    }

    /**
     * Gets the path of the request.
     *
     * @return the request path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Set the request path.
     *
     * @param path the path to be set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Gets the request body.
     *
     * @return the body of the request
     */
    public String getBody()
    {
        return body;
    }

    /**
     * Sets the request body.
     *
     * @param body the body to be set
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * Gets the http action.
     *
     * @return the request's http verb
     */
    public String getHttpVerb()
    {
        return httpVerb;
    }

    /**
     * Sets the http action.
     *
     * @param httpVerb the http request type
     */
    public void setHttpVerb(String httpVerb)
    {
        this.httpVerb = httpVerb;
    }

    /**
     * Gets the response codes for which the service is considered to be healthy.
     *
     * @return the accepted response codes
     */
    public List<Integer> getAcceptedStatusCodes()
    {
        return acceptedStatusCodes;
    }

    /**
     * Sets the accepted response status codes.
     *
     * @param acceptedStatusCodes a list of accepted resonse status codes
     */
    public void setAcceptedStatusCodes(List<Integer> acceptedStatusCodes)
    {
        this.acceptedStatusCodes = acceptedStatusCodes;
    }
}
