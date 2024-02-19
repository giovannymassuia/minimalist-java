package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class TokenBucketTest {

    @Test
    void check() throws InterruptedException {
        int bucketSize = 4;
        var refillRate = Duration.ofSeconds(2);
        RateLimiter rl = RateLimitFactory.customTokenBucket(bucketSize, refillRate);

        /*
            Scenario:
            - Bucket uses the default max capacity
            - Makes request to consume all tokens
            - Make more requests, bucket will be empty, should not go through
            - wait bucket to refill, and make more request
         */

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t1
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t2
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t3
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4

        Thread.sleep(1000);

        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t6
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t7

        Thread.sleep(3000);

        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t8
        assertTrue(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t9

        assertEquals(2, ((TokenBucket) rl).getAvailableTokensCount());
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {
    }

}
