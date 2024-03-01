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
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class SlidingWindowCounterSlots implements RateLimiter {

    private static final int DEFAULT_MAX_REQUESTS = 10;
    private static final Duration DEFAULT_WINDOW_SIZE = Duration.ofSeconds(1);
    private static final int SLOTS = 6; // Number of slots in the window

    private final int maxRequestsPerWindow;
    private final long windowSizeMillis;

    private final Deque<AtomicInteger> slots;
    private final Object lock = new Object();

    private final AtomicLong lastCleanup;

    public SlidingWindowCounterSlots() {
        this(DEFAULT_MAX_REQUESTS, DEFAULT_WINDOW_SIZE);
    }

    public SlidingWindowCounterSlots(int maxRequests, Duration windowSize) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("Max requests cannot be less or equal to zero.");
        }
        if (windowSize == null) {
            throw new IllegalArgumentException("Window size should not be null.");
        }

        this.maxRequestsPerWindow = maxRequests;
        this.windowSizeMillis = windowSize.toMillis();
        this.lastCleanup = new AtomicLong(System.currentTimeMillis());

        this.slots = new LinkedList<>();
        for (int i = 0; i < SLOTS; i++) {
            slots.add(new AtomicInteger(0)); // Initialize slots with zero count
        }
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        boolean allowed = false;
        synchronized (lock) {
            long now = System.currentTimeMillis();
            slideWindow(now);

            int totalRequests = slots.stream().mapToInt(AtomicInteger::get).sum();
            if (totalRequests < maxRequestsPerWindow) {
                incrementCurrentSlot();
                allowed = true;
            }
        }

        return processRequest(allowed, requestRunnable);
    }

    private void slideWindow(long now) {
        long lastUpdate = lastCleanup.get();
        long elapsedSinceLastUpdate = now - lastUpdate;

        // Calculate the number of whole window periods that have elapsed since the last update
        long windowsSinceLastUpdate = elapsedSinceLastUpdate / windowSizeMillis;

        if (windowsSinceLastUpdate > 0) {
            int slotsToSlide = (int) Math.min(windowsSinceLastUpdate, SLOTS);

            for (int i = 0; i < slotsToSlide; i++) {
                slots.poll(); // Remove the oldest slot
                slots.add(new AtomicInteger(0)); // Add a new slot for the current period
            }

            // Update lastCleanup to the start of the current window period
            lastCleanup.addAndGet(windowsSinceLastUpdate * windowSizeMillis);
        }
    }

    private void incrementCurrentSlot() {
        AtomicInteger currentSlot = slots.peekLast(); // Get the current slot without removing it
        if (currentSlot != null) {
            currentSlot.incrementAndGet(); // Increment the count atomically
        }
    }

    @Override
    public void shutdownGracefully() {
        // Implement any necessary shutdown logic here
    }

    protected Queue<AtomicInteger> getSlots() {
        return slots;
    }
}
