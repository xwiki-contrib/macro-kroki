package org.xwiki.contrib.kroki.configuration;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

@Role
@Unstable
public interface DiagramGeneratorConfiguration {
    /**
     * @return the Docker image used to create the Docker container running the Kroki API; defaults to
     *         "{@code yuzutech/kroki}"
     */
    String getKrokiDockerImage();

    /**
     * @return the name of the Docker container running the Kroki API used to generate graph images from string content;
     *         this is also used as a network-scoped alias for the container; defaults to
     *         "{@code kroki-1}"
     */
    String getKrokiDockerContainerName();

    /**
     * @return {@code true} if the Docker container running the Kroki API can be reused across XWiki
     *         restarts, {@code false} to remove the container each time XWiki is stopped / restarted; defaults to
     *         {@code false}
     */
    boolean isKrokiDockerContainerReusable();

    /**
     * @return the name or id of the Docker network to add the Chrome Docker container to; this is useful when XWiki
     *         itself runs inside a Docker container and you want to have the Chrome container in the same network in
     *         order for them to communicate, see {@link #getXWikiHost()}; defaults to "{@code bridge}" the default
     *         Docker network
     * @see #getXWikiHost()
     */
    String getDockerNetwork();

    /**
     * @return the host running the Kroki API, specified either by its name or by its IP address; this
     *         allows you to use a remote API instance, running on a separate machine, rather than a API instance
     *         running in a Docker container on the same machine; defaults to empty value, meaning that by default the
     *         graph generation is done using the Kroki API instance running in the Docker container specified by
     *         {@link #getKrokiDockerContainerName()}
     */
    String getKrokiHost();

    /**
     * @return the port number used for communicating with the Kroki API running on the host specified
     *         by {@link #getKrokiHost()}; defaults to {@code 8000}
     */
    int getKrokiRemoteDebuggingPort();

    /**
     * @return the host name or IP address that the Kroki API should use to access the XWiki instance
     *         ; defaults to "{@code host.xwiki.internal}" which means the host running the
     *         Docker daemon; if XWiki runs itself inside a Docker container then you should use the assigned network
     *         alias, provided both containers (XWiki and Kroki) are in the same Docker network, specified by
     *         {@link #getDockerNetwork()};
     */
    String getXWikiHost();
}
