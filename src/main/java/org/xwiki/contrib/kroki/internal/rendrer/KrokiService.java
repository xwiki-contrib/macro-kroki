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
package org.xwiki.contrib.kroki.internal.rendrer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.kroki.configuration.KrokiMacroConfiguration;
import org.xwiki.contrib.kroki.utils.HealthCheckRequestParameters;

/**
 * Supports interaction with Kroki API endpoints.
 *
 * @version $Id$
 */
@Component(roles = KrokiService.class)
@Singleton
public class KrokiService
{
    private static final int CONNECTION_TIMEOUT = 10;

    private static final String HTTP_PROTOCOL = "http://";

    private static final String REQUEST_METHOD = "POST";

    @Inject
    private Logger logger;

    private String host;

    private int port;

    /**
     * Sets up the paramaters to access a service.
     *
     * @param host the host address
     * @param config the configuration of the docker container
     * @throws TimeoutException if the service specified by the container does not respond in a period of time
     */
    public void connect(String host, KrokiMacroConfiguration config) throws TimeoutException
    {
        this.host = host;
        this.port = config.getKrokiPort();

        this.logger.debug("Connecting to the Kroki server on [{}:{}].", host, port);

        waitForKrokiService(CONNECTION_TIMEOUT, config.getHealthCheckRequest());
    }

    /**
     * Calls the Kroki API to generate a diagram from it's text representation according to the used library.
     *
     * @param diagramLib the diagram library to be used
     * @param imgFormat the format of the generated image
     * @param graphContent the content to be transformed
     * @return the image's input stream
     */
    public InputStream renderDiagram(String diagramLib, String imgFormat, String graphContent)
    {
        try {
            String url = HTTP_PROTOCOL + host + ':' + port;
            String path = '/' + diagramLib + '/' + imgFormat;
            HttpURLConnection conn = createRequest(url, path, REQUEST_METHOD, graphContent);
            return conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForKrokiService(int timeoutSeconds, HealthCheckRequestParameters healthParams)
        throws TimeoutException
    {
        this.logger.debug("Waiting [{}] seconds to get our first response from kroki.", timeoutSeconds);

        int timeoutMillis = timeoutSeconds * 1000;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                String url = HTTP_PROTOCOL + host + ':' + port;
                HttpURLConnection con =
                    createRequest(url, healthParams.getPath(), healthParams.getHttpVerb(), healthParams.getBody());
                int statusCode = con.getResponseCode();
                if (healthParams.getAcceptedStatusCodes().contains(statusCode)) {
                    return;
                }
            } catch (Exception ignored) {
            }
            this.logger.debug("Kroki serevice not available. Retrying in 2s.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                this.logger.warn("Interrupted thread [{}]. Root cause: [{}].", Thread.currentThread().getName(),
                    ExceptionUtils.getRootCauseMessage(ie));
                // Restore the interrupted state.
                Thread.currentThread().interrupt();
            }
        }

        long waitTime = (System.currentTimeMillis() - start) / 1000;
        throw new TimeoutException(
            String.format("Timeout waiting for Kroki seervice to become available. Waited [%s] seconds.", waitTime));
    }

    private HttpURLConnection createRequest(String url, String path, String httpVerb, String body) throws IOException
    {
        URL connectionURL = new URL(url + path);
        HttpURLConnection con = (HttpURLConnection) connectionURL.openConnection();
        con.setRequestMethod(httpVerb);
        if (httpVerb.equals(REQUEST_METHOD) || httpVerb.equals("PUT")) {
            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            con.setFixedLengthStreamingMode(out.length);
            con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            con.setDoOutput(true);
            con.connect();
            try (OutputStream os = con.getOutputStream()) {
                os.write(out);
            }
        } else {
            con.connect();
        }

        return con;
    }
}
