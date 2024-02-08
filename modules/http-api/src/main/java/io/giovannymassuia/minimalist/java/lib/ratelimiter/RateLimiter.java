package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

public interface RateLimiter {

    boolean check(RoutePath routePath);

}
