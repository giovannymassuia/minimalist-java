package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class FixedWindowCounterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    public void setUp() {
        // Set up the rate limiter with a small window for testing purposes
        // Here we're using a 100ms window and a limit of 3 requests per window
        rateLimiter = RateLimitFactory.customFixedWindowCounter(3, Duration.ofMillis(100));
    }

    @Test
    public void allowsRequestsUpToLimit() {
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));

        // The fourth request should be rejected as it exceeds the limit of 3
        assertFalse(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
    }

    @Test
    public void resetsCounterAtNewWindow() throws InterruptedException {
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));

        // Wait for the next window
        Thread.sleep(100);

        // The counter should reset, allowing new requests
        assertTrue(rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun));
    }

    @Test
    public void handlesConcurrentRequests() throws InterruptedException {
        Thread[] threads = new Thread[5];
        final boolean[] results = new boolean[5];

        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = rateLimiter.checkAndProcess(buildRoutePath(), this::emptyRun);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assuming all threads ran within the same window, only the first 3 should be allowed
        int allowedRequests = 0;
        for (boolean result : results) {
            if (result) {
                allowedRequests++;
            }
        }

        assertEquals(3, allowedRequests);
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {}
}
