package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class BlockAllRequests implements RateLimiter {

    @Override
    public boolean check(RoutePath routePath) {
        return false;
    }
}
