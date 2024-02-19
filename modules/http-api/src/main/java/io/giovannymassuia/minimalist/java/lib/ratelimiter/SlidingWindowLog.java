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

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

class SlidingWindowLog implements RateLimiter {

    private final Logger logger = Logger.getLogger(SlidingWindowLog.class.getName());

    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_THRESHOLD_SECONDS = 10;

    private final int capacity;
    private final int thresholdSeconds;

    private final BlockingQueue<Long> windowLog;

    SlidingWindowLog() {
        this(DEFAULT_CAPACITY, DEFAULT_THRESHOLD_SECONDS);
    }

    SlidingWindowLog(int capacity, int thresholdSeconds) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity should not be less than 0.");
        }
        if (thresholdSeconds <= 1) {
            throw new IllegalArgumentException("Capacity should not be less than 1 second.");
        }

        this.capacity = capacity;
        this.thresholdSeconds = thresholdSeconds;
        this.windowLog = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable requestRunnable) {
        long timestamp = Date.from(Instant.now()).getTime();

        synchronized (windowLog) {
            while (!windowLog.isEmpty()
                && windowLog.peek() < (timestamp - (thresholdSeconds * 1000L))) {
                windowLog.poll();
            }

            if (windowLog.size() <= capacity) {
                windowLog.add(timestamp);
            } else {
                logger.info("windowLog at capacity [%d]. Head timestamp [%d]"
                    .formatted(getWindowSize(), windowLog.peek()));
            }
        }

        return processRequest(windowLog.size() <= capacity, requestRunnable);
    }

    @Override
    public void shutdownGracefully() {

    }

    int getWindowSize() {
        return this.windowLog.size();
    }
}
