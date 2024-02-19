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

class SlidingWindowCounterApproximate implements RateLimiter {

    private static final int DEFAULT_MAX_REQUESTS = 10;
    private static final Duration DEFAULT_WINDOW_SIZE = Duration.ofSeconds(1);

    private final long windowSizeMillis;
    private final int maxRequests;
    private final AtomicInteger currentWindowCount;
    private final AtomicInteger previousWindowCount;
    private final AtomicLong windowStart;

    public SlidingWindowCounterApproximate() {
        this(DEFAULT_MAX_REQUESTS, DEFAULT_WINDOW_SIZE);
    }

    public SlidingWindowCounterApproximate(int maxRequests, Duration windowSize) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("Max requests must be greater than zero.");
        }
        if (windowSize == null) {
            throw new IllegalArgumentException("Window Size should not be null");
        }
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSize.toMillis();

        this.currentWindowCount = new AtomicInteger(0);
        this.previousWindowCount = new AtomicInteger(0);
        this.windowStart = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        long now = System.currentTimeMillis();
        long currentWindowStart = now - (now % windowSizeMillis);

        // Check if we've moved to a new window
        if (windowStart.get() != currentWindowStart) {
            synchronized (this) {
                if (windowStart.compareAndSet(windowStart.get(), currentWindowStart)) {
                    // Update the counts for the new window
                    previousWindowCount.set(currentWindowCount.get());
                    currentWindowCount.set(0);
                }
            }
        }

        // Calculate the total count with weighting
        double positionInWindow = (double) (now - windowStart.get()) / windowSizeMillis;
        double weightedPreviousCount = previousWindowCount.get() * (1 - positionInWindow);
        double totalCount = currentWindowCount.get() + weightedPreviousCount;

        boolean allowed = false;
        if (totalCount < maxRequests) { // Check against the maxRequests threshold
            currentWindowCount.incrementAndGet();
            allowed = true;
        }
        return processRequest(allowed, requestRunnable);
    }

    @Override
    public void shutdownGracefully() {
        // Implement any necessary shutdown logic here
    }
}
