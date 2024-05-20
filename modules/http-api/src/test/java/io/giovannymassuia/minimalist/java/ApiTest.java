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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.servers.Api;

class ApiTest {

    private int randomPort;

    @BeforeEach
    void setUp() {
        randomPort = (int) (Math.random() * 10000) + 10000;
        Api.create(randomPort)
                .addRoute(Route.builder("/api")
                        .path(RouteMethod.GET, "/",
                                ctx -> ResponseEntity.ok(Map.of("message", "Hello World!")))
                        .path(RouteMethod.GET,
                                "/{name}",
                                ctx -> ResponseEntity.ok(Map.of("message",
                                        "Hello " + ctx.pathParams().get("name")))))
                .start();
    }

    @Test
    void testJavaHttpApi() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + randomPort + "/api"))
                .GET()
                .build();

        try (var client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            assertEquals("{\"message\":\"Hello World!\"}", response.body());
        }
    }

    @Test
    void testJavaHttpApiPathParam() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + randomPort + "/api/john-doe"))
                .GET()
                .build();

        try (var client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            assertEquals("{\"message\":\"Hello john-doe\"}", response.body());
        }
    }
}
