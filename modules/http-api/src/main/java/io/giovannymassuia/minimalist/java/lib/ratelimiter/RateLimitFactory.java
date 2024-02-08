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

public class RateLimitFactory {

    public static RateLimiter blockAllRequests() {
        return new BlockAllRequests();
    }

    public static RateLimiter defaultSlidingWindowLog() {
        return new SlidingWindowLog();
    }

    public static RateLimiter customSlidingWindowLog(int capacity, int thresholdSeconds) {
        return new SlidingWindowLog(capacity, thresholdSeconds);
    }
}
