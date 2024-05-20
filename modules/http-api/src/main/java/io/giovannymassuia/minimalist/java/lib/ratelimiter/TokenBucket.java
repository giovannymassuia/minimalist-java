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
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import io.giovannymassuia.minimalist.java.lib.route.RoutePath;

/**
 * Token Bucket algorithm implementation.
 */
class TokenBucket implements RateLimiter {

    private final Logger logger = Logger.getLogger(TokenBucket.class.getName());

    private static final int DEFAULT_BUCKET_SIZE = 5;
    private static final Duration DEFAULT_REFILL_RATE = Duration.ofSeconds(3);
    private static final int DEFAULT_TOKENS_PER_REFILL = 5;

    private final int bucketSize;
    private final long refillRateMilliseconds;
    private final int tokensPerRefill;

    private final AtomicInteger bucket;
    private final AtomicLong lastRefillTimestamp;

    private final ScheduledExecutorService scheduler;

    TokenBucket() {
        this(DEFAULT_BUCKET_SIZE, DEFAULT_REFILL_RATE, DEFAULT_TOKENS_PER_REFILL, false);
    }

    TokenBucket(int bucketSize, Duration refillRate, int tokensPerRefill) {
        this(bucketSize, refillRate, tokensPerRefill, false);
    }

    TokenBucket(int bucketSize, Duration refillRate, int tokensPerRefill, boolean useScheduler) {
        if (bucketSize <= 0)
            throw new IllegalArgumentException("Bucket size can not be less or equal to zero.");
        if (tokensPerRefill < 1)
            throw new IllegalArgumentException("Tokens per refill can not be less than 1.");
        if (tokensPerRefill > bucketSize)
            throw new IllegalArgumentException(
                    "Tokens per refill can not be greater than bucket size.");
        if (refillRate == null)
            throw new IllegalArgumentException("Refill rate should not be null.");

        this.bucketSize = bucketSize;
        this.refillRateMilliseconds = refillRate.toMillis();
        this.tokensPerRefill = tokensPerRefill;

        this.bucket = new AtomicInteger(bucketSize);
        this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());

        if (useScheduler) {
            this.scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(this::refill, 0, refillRateMilliseconds,
                    TimeUnit.MILLISECONDS);
        } else {
            this.scheduler = null;
        }
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        if (scheduler == null) {
            refill();
        }

        return processRequest(bucket.get() > 0 && bucket.decrementAndGet() >= 0, requestRunnable);
    }

    @Override
    public void shutdownGracefully() {
        if (scheduler == null)
            return;

        System.out.println("Shutting down " + this.getClass().getSimpleName() + "...");

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }

        System.out.println(this.getClass().getSimpleName() + " terminated scheduler.");
    }

    private synchronized void refill() {
        long now = System.currentTimeMillis();
        long lastRefill = lastRefillTimestamp.get();
        long elapsedTime = now - lastRefill + 100; // Add 100ms to make sure we don't miss any
                                                   // tokens
        if (elapsedTime >= refillRateMilliseconds) {
            int newBucketSize = Math.min(bucketSize, bucket.get() + tokensPerRefill);
            bucket.set(newBucketSize);
            lastRefillTimestamp.set(now);
        }

        logger.fine("Bucket refilled to , total [" + bucket.get() + "] tokens at "
                + LocalDateTime.now() + ".");
    }

    int getAvailableTokensCount() {
        return bucket.get();
    }
}
