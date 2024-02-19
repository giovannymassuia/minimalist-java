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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;
import org.junit.jupiter.api.Test;

class SlidingWindowLogTest {

    @Test
    void check() throws InterruptedException {
        RateLimiter rl = RateLimitFactory.customSlidingWindowLog(3, 3);

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t1
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t2
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t3
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4

        Thread.sleep(1000);

        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t6
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t7

        Thread.sleep(3000);

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t6
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t7

        assertEquals(2, ((SlidingWindowLog) rl).getWindowSize());
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {
    }
}
