package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class LeakingBucketTest {

    @Test
    void check() throws InterruptedException {
        int bucketSize = 2;
        var leakRate = Duration.ofSeconds(2);
        RateLimiter rl = RateLimitFactory.customLeakingBucket(bucketSize, leakRate);

        AtomicBoolean r1 = new AtomicBoolean(false);
        AtomicBoolean r2 = new AtomicBoolean(false);

        assertTrue(rl.checkAndProcess(buildRoutePath(), () -> r1.set(true))); // t1
        assertTrue(rl.checkAndProcess(buildRoutePath(), () -> r2.set(true))); // t2
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t4

        Thread.sleep(1000);

        assertFalse(r1.get());
        assertFalse(r2.get());
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t6
        assertFalse(rl.checkAndProcess(buildRoutePath(), this::emptyRun)); // t7

        Thread.sleep(3000);

        assertEquals(0, ((LeakingBucket) rl).getQueueSize());

        AtomicBoolean r3 = new AtomicBoolean(false);
        assertTrue(rl.checkAndProcess(buildRoutePath(), () -> r3.set(true))); // t8

        assertTrue(r1.get());
        assertTrue(r2.get());
        assertFalse(r3.get());

        assertEquals(1, ((LeakingBucket) rl).getQueueSize());
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }

    private void emptyRun() {
    }

}
