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

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class TokenBucket implements RateLimiter {

    private static final int DEFAULT_BUCKET_SIZE = 5;
    private static final Duration DEFAULT_REFILL_RATE = Duration.ofSeconds(3);

    private final int bucketSize;
    private final long refillRateMilliseconds;

    private final AtomicInteger bucket;
    private final AtomicLong lastRefillTimestamp;

    TokenBucket() {
        this(DEFAULT_BUCKET_SIZE, DEFAULT_REFILL_RATE);
    }

    TokenBucket(int bucketSize, Duration refillRate) {
        if (bucketSize <= 0) {
            throw new IllegalArgumentException("Bucket size can not be less or equal to zero.");
        }
        if (refillRate == null) {
            throw new IllegalArgumentException("Refill rate should not be null.");
        }
        this.bucketSize = bucketSize;
        this.refillRateMilliseconds = refillRate.toMillis();

        this.bucket = new AtomicInteger(bucketSize);
        this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        refill();
        return processRequest(bucket.get() > 0 && bucket.decrementAndGet() >= 0, requestRunnable);
    }

    @Override
    public void shutdownGracefully() {

    }

    private void refill() {
        long now = System.currentTimeMillis();
        long lastRefill = lastRefillTimestamp.get();
        long elapsedTime = now - lastRefill;
        if (elapsedTime >= refillRateMilliseconds) {
            bucket.set(bucketSize); // refill back with max
            lastRefillTimestamp.set(now);
        }
    }

    int getAvailableTokensCount() {
        return bucket.get();
    }
}
