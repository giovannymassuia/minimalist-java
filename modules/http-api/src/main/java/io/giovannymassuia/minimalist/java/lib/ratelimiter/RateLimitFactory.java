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
