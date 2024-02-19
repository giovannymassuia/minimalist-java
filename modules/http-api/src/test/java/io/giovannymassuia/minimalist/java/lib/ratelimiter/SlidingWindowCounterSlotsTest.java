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
package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class SlidingWindowCounterSlotsTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        // Initialize the rate limiter with a small window for quick testing
        // For example, 100ms window with 10 max requests
        rateLimiter = RateLimitFactory.customSlidingWindowCounterSlots(10, Duration.ofMillis(100));
    }

    @Test
    void allowsRequestsUpToLimit() {
        for (int i = 0; i < 10; i++) {
            boolean allowed = rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun);
            assertTrue(allowed, "Request " + (i + 1) + " within limit should be allowed");
        }

        // The 11th request should be rejected as it exceeds the limit
        assertFalse(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
    }

    @Test
    void respectsSlidingWindow() throws InterruptedException {
        // Initially, send some requests
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        }

        // Wait to partially slide the window
        Thread.sleep(50); // Wait for half of the window duration

        // Send additional requests and verify that they are allowed due to sliding window effect
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        }
    }

    @Test
    void transitionsBetweenWindows() throws InterruptedException {
        // Fill up the rate limit for the current window
        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        }

        // Wait for the window to fully slide
        Thread.sleep(100); // Wait for the window size to pass

        // The rate limiter should allow new requests in the new window
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {}
}
