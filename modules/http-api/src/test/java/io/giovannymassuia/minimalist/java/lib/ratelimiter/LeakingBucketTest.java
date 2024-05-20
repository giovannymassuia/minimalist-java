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
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.route.RoutePath;

class LeakingBucketTest {

    @Test
    void check() throws InterruptedException {
        int bucketSize = 2;
        var leakRate = Duration.ofSeconds(2);
        RateLimiter rl = RateLimitFactory.customLeakingBucket(bucketSize, leakRate, 1);

        AtomicBoolean r1 = new AtomicBoolean(false);
        AtomicBoolean r2 = new AtomicBoolean(false);

        assertTrue(rl.checkAndProcess(buildRoutePath(), () -> r1.set(true))); // t1
        assertTrue(rl.checkAndProcess(buildRoutePath(), () -> r2.set(true))); // t2
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4

        Thread.sleep(1000);

        assertFalse(r1.get());
        assertFalse(r2.get());
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t6
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t7

        Thread.sleep(3000);

        assertEquals(0, ((LeakingBucket) rl).getQueueSize());

        AtomicBoolean r3 = new AtomicBoolean(false);
        assertTrue(rl.checkAndProcess(buildRoutePath(), () -> r3.set(true))); // t8

        assertTrue(r1.get());
        assertTrue(r2.get());
        assertFalse(r3.get());

        assertEquals(1, ((LeakingBucket) rl).getQueueSize());
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {
    }

}
