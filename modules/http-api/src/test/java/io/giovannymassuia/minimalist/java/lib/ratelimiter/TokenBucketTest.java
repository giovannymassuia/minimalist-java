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

import java.time.Duration;

import org.junit.jupiter.api.Test;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class TokenBucketTest {

    @Test
    void check() throws InterruptedException {
        int bucketSize = 4;
        var refillRate = Duration.ofSeconds(2);
        RateLimiter rl = RateLimitFactory.customTokenBucket(bucketSize, refillRate, bucketSize);

        /*
         * Scenario: - Bucket uses the default max capacity - Makes request to consume all tokens -
         * Make more requests, bucket will be empty, should not go through - wait bucket to refill,
         * and make more request
         */

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t1
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t2
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t3
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4

        Thread.sleep(1000);

        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t6
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t7

        Thread.sleep(3000);

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t8
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t9

        assertEquals(2, ((TokenBucket) rl).getAvailableTokensCount());
    }

    @Test
    void checkWithScheduler() throws InterruptedException {
        RateLimiter rl = RateLimitFactory.customTokenBucketWithScheduler(4, Duration.ofSeconds(1),
                1);

        /*
         * Scenario: - Bucket uses the default max capacity - Makes request to consume all tokens -
         * Make more requests, bucket will be empty, should not go through - wait bucket to refill,
         * and make more request
         */

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t1
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t2
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t3
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4

        Thread.sleep(1500);

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t8
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t9

        assertEquals(0, ((TokenBucket) rl).getAvailableTokensCount());
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {
    }

}
