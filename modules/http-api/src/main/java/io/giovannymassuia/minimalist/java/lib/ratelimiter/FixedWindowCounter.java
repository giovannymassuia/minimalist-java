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

import io.giovannymassuia.minimalist.java.lib.route.RoutePath;

class FixedWindowCounter implements RateLimiter {

    private static final int DEFAULT_BUCKET_SIZE = 5;
    private static final Duration DEFAULT_WINDOW_SIZE_MILLIS = Duration.ofSeconds(1);

    private final int maxRequests;
    private final long windowSizeMillis;

    private final AtomicInteger requestCount;
    private final AtomicLong windowStart;

    public FixedWindowCounter() {
        this(DEFAULT_BUCKET_SIZE, DEFAULT_WINDOW_SIZE_MILLIS);
    }

    public FixedWindowCounter(int maxRequests, Duration windowSize) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("Bucket size cannot be less or equal to zero.");
        }
        if (windowSize == null) {
            throw new IllegalArgumentException("Refill rate should not be null.");
        }

        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSize.toMillis();

        this.requestCount = new AtomicInteger(0);
        this.windowStart = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        long now = System.currentTimeMillis();

        // ensures that the window is aligned to "round" times based on the window size
        // (e.g., every second, minute, hour, etc...)
        long currentWindowStart = now - (now % windowSizeMillis);

        if (windowStart.get() != currentWindowStart
                && windowStart.compareAndSet(windowStart.get(), currentWindowStart)) {
            requestCount.set(0); // Start of a new window, reset the counter
        }

        if (requestCount.incrementAndGet() <= maxRequests) {
            return processRequest(true, requestRunnable); // Process the request
        } else {
            // Revert the count increment since the request is not processed
            requestCount.decrementAndGet();
            return false; // Request rejected
        }
    }

    @Override
    public void shutdownGracefully() {
    }

    public int getAvailableTokensCount() {
        return maxRequests - requestCount.get();
    }
}
