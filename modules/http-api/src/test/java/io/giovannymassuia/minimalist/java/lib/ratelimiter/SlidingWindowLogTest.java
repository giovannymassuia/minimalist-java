package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route.RouteMethod;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class SlidingWindowLogTest {

    @Test
    void check() throws InterruptedException {
        RateLimiter rl = RateLimitFactory.customSlidingWindowLog(3, 3);

        assertTrue(rl.check(buildRoutePath())); // t1
        assertTrue(rl.check(buildRoutePath())); // t2
        assertTrue(rl.check(buildRoutePath())); // t3
        assertFalse(rl.check(buildRoutePath())); // t4

        Thread.sleep(1000);

        assertFalse(rl.check(buildRoutePath())); // t6
        assertFalse(rl.check(buildRoutePath())); // t7

        Thread.sleep(3000);

        assertTrue(rl.check(buildRoutePath())); // t6
        assertTrue(rl.check(buildRoutePath())); // t7

        assertEquals(2, ((SlidingWindowLog) rl).getWindowSize());
    }

    private RoutePath buildRoutePath() {
        return new RoutePath(RouteMethod.GET.name(), "/", ctx -> ResponseEntity.ok(""));
    }
}
