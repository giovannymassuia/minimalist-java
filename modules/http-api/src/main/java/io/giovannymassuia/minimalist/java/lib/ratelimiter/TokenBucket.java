package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
        this.refillRateMilliseconds = refillRate.getSeconds() * 1000;

        this.bucket = new AtomicInteger(bucketSize);
        this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean check(RoutePath routePath) {
        refill();
        return bucket.get() > 0 && bucket.decrementAndGet() >= 0;
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
