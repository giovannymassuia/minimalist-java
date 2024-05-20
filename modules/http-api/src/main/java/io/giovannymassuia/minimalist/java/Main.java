/*
 * Copyright 2024 minimalist-java
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
package io.giovannymassuia.minimalist.java;

import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.ratelimiter.RateLimitFactory;
import io.giovannymassuia.minimalist.java.lib.route.Route;
import io.giovannymassuia.minimalist.java.lib.route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.servers.Api;

public class Main {

    public static void main(String[] args) {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.FINE);

        Api.create(8080)
                // .rateLimit(RateLimitFactory.customTokenBucketWithScheduler(5,
                // Duration.ofSeconds(2), 5))
                // .rateLimit(RateLimitFactory.customTokenBucket(5, Duration.ofSeconds(2), 5))
                .rateLimit(RateLimitFactory.customLeakingBucket(5, Duration.ofSeconds(1), 1))
                .addRoute(Route.builder("/")
                        .path(RouteMethod.GET, "/",
                                ctx -> ResponseEntity.ok(Map.of("message", "Hello World!"))))
                .start();
    }

}
