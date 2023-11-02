package io.giovannymassuia.minimalist.java.lib.servers;

import io.giovannymassuia.minimalist.java.lib.Route;

public interface ApiServer {

    void create(int port);

    void addRoute(Route route);

    void start();

    static ApiServer defaultServer(int port) {
        var api = new JavaHttpApi();
        api.create(port);
        return api;
    }
}
