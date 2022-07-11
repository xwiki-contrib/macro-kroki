package org.xwiki.contrib.kroki.generator;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component(roles = KrokiConnectionManager.class)
@Singleton
public class KrokiConnectionManager {
    @Inject
    private Logger logger;

    private String host;
    private int remoteDebuggingPort;

    private static final int REMOTE_DEBUGGING_TIMEOUT = 10;

    public void setup(String host, int remoteDebuggingPort) throws TimeoutException {
        this.logger.debug("Connecting to the Kroki server on [{}:{}].", host, remoteDebuggingPort);
        this.host = host;
        this.remoteDebuggingPort = remoteDebuggingPort;
        waitForKrokiService(REMOTE_DEBUGGING_TIMEOUT);
    }

    private void waitForKrokiService(int timeoutSeconds) throws TimeoutException
    {
        this.logger.debug("Waiting [{}] seconds to get our first response from kroki.", timeoutSeconds);

        int timeoutMillis = timeoutSeconds * 1000;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                URL url = new URL("http://" + host + ':' + remoteDebuggingPort);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                int statusCode = con.getResponseCode();
                if(statusCode != 200){
                    throw new RuntimeException("Code received not 200");
                }
                return;
            } catch (Exception e) {
                this.logger.debug("Chrome remote debugging not available. Retrying in 2s.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    this.logger.warn("Interrupted thread [{}]. Root cause: [{}].", Thread.currentThread().getName(),
                            ExceptionUtils.getRootCauseMessage(e));
                    // Restore the interrupted state.
                    Thread.currentThread().interrupt();
                }
            }
        }

        long waitTime = (System.currentTimeMillis() - start) / 1000;
        throw new TimeoutException(String
                .format("Timeout waiting for Chrome remote debugging to become available. Waited [%s] seconds.", waitTime));
    }

    public InputStream generateDiagram(String diagramLib, String imgFormat, String graphContent){
        try {
            URL url = new URL("http://" + host + ':' + remoteDebuggingPort + '/' + diagramLib+ '/' + imgFormat);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            byte[] out = graphContent.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(out.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setDoOutput(true);
            conn.connect();
            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            return conn.getInputStream();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
