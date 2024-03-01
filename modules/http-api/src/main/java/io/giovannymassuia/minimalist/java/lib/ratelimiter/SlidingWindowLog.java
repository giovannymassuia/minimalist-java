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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class SlidingWindowLog implements RateLimiter {

    private final Logger logger = Logger.getLogger(SlidingWindowLog.class.getName());

    private static final int DEFAULT_CAPACITY = 10;
    private static final Duration DEFAULT_THRESHOLD = Duration.ofSeconds(10);

    private final int capacity;
    private final long thresholdMilliseconds;

    private final BlockingQueue<Long> windowLog;

    private final ScheduledExecutorService scheduler;

    SlidingWindowLog() {
        this(DEFAULT_CAPACITY, DEFAULT_THRESHOLD);
    }

    SlidingWindowLog(int capacity, Duration threshold) {
        this(capacity, threshold, false);
    }

    SlidingWindowLog(int capacity, Duration threshold, boolean useScheduler) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity should not be less than 0.");
        }
        if (threshold == null) {
            throw new IllegalArgumentException("Threshold should not be null.");
        }

        this.capacity = capacity;
        this.thresholdMilliseconds = threshold.toMillis();
        this.windowLog = new LinkedBlockingQueue<>();

        if (useScheduler) {
            this.scheduler = Executors.newScheduledThreadPool(1);
            // Cleanup at half the window size
            long cleanupInterval = thresholdMilliseconds / 2;
            // Schedule the cleanup task to run periodically
            scheduler.scheduleAtFixedRate(this::cleanupOldRequests, 0, cleanupInterval, TimeUnit.MILLISECONDS);
        } else {
            this.scheduler = null;
        }
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        long now = System.currentTimeMillis();
        boolean allowRequest = false;

        synchronized (this) {
            cleanupOldRequests();

            // in window log, if the size is less than the capacity, we allow the request
            // and add the current timestamp to the window log
            // if the size is equal to the capacity, we do not allow the request
            // but we still add the current timestamp to the window log
            windowLog.offer(now);

            if (windowLog.size() <= capacity) {
                allowRequest = true;
            } else {
                logger.info(
                        "windowLog at capacity [%d]. Head timestamp [%d]".formatted(getWindowSize(), windowLog.peek()));
            }
        }

        return processRequest(allowRequest, requestRunnable);
    }

    private void cleanupOldRequests() {
        long now = System.currentTimeMillis();
        while (!windowLog.isEmpty() && windowLog.peek() < (now - thresholdMilliseconds)) {
            windowLog.poll();
        }
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

    int getWindowSize() {
        return this.windowLog.size();
    }
}
