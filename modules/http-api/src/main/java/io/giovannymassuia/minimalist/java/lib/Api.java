/*
 * Copyright 2023 minimalist-java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.giovannymassuia.minimalist.java.lib;

import io.giovannymassuia.minimalist.java.lib.servers.ApiServer;
import java.util.logging.Logger;

public class Api {

    private final Logger logger = Logger.getLogger(Api.class.getName());

    private final int port;
    private final ApiServer apiServer;

    private Api(int port, ApiServer apiServer) {
        this.port = port;
        this.apiServer = apiServer;
    }

    public static Api create(int port) {
        return new Api(port, ApiServer.defaultServer(port));
    }

    public Api addRoute(Route route) {
        apiServer.addRoute(route);
        return this;
    }

    public void start() {
        apiServer.start();
        logger.info(
            "Server [%s] started on port %d".formatted(apiServer.getClass().getSimpleName(), port));
    }

}
