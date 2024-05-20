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

public class RateLimitFactory {

    public static RateLimiter blockAllRequests() {
        return new BlockAllRequests();
    }

    public static RateLimiter defaultSlidingWindowLog() {
        return new SlidingWindowLog();
    }

    public static RateLimiter customSlidingWindowLog(int capacity, Duration threshold) {
        return new SlidingWindowLog(capacity, threshold);
    }

    public static RateLimiter customSlidingWindowLogWithScheduler(int capacity,
            Duration threshold) {
        return new SlidingWindowLog(capacity, threshold, true);
    }

    public static RateLimiter defaultTokenBucket() {
        return new TokenBucket();
    }

    public static RateLimiter customTokenBucket(int bucketSize, Duration refillRate,
            int tokensPerRefill) {
        return new TokenBucket(bucketSize, refillRate, tokensPerRefill);
    }

    public static RateLimiter customTokenBucketWithScheduler(int bucketSize, Duration refillRate,
            int tokensPerRefill) {
        return new TokenBucket(bucketSize, refillRate, tokensPerRefill, true);
    }

    public static RateLimiter defaultLeakingBucket() {
        return new LeakingBucket();
    }

    public static RateLimiter customLeakingBucket(int bucketSize, Duration leakRate,
            int requestsPerLeak) {
        return new LeakingBucket(bucketSize, leakRate, requestsPerLeak);
    }

    public static RateLimiter defaultFixedWindowCounter() {
        return new FixedWindowCounter();
    }

    public static RateLimiter customFixedWindowCounter(int maxRequests, Duration windowSize) {
        return new FixedWindowCounter(maxRequests, windowSize);
    }

    public static RateLimiter defaultSlidingWindowCounterSlots() {
        return new SlidingWindowCounterSlots();
    }

    public static RateLimiter customSlidingWindowCounterSlots(int maxRequests,
            Duration windowSize) {
        return new SlidingWindowCounterSlots(maxRequests, windowSize);
    }

    public static RateLimiter defaultSlidingWindowCounterApproximate() {
        return new SlidingWindowCounterApproximate();
    }

    public static RateLimiter customSlidingWindowCounterApproximate(int maxRequests,
            Duration windowSize) {
        return new SlidingWindowCounterApproximate(maxRequests, windowSize);
    }
}
