package com.example.httpserver.server;

import com.example.httpserver.server.handler.webfs.HttpServerHandler;

import java.util.*;

public abstract class ServerBuilder {

    private Map<String, Route> routes;

    private ServerBuilder() {

    }

    public RouteBuilder context(String context) {
        return new RouteBuilder(this, context);
    }


    public void build() {

    }

    private void add(String context, Set<Route> route) {

    }

    private static class Route {
        private final String action;
        private final String path;
        private final HttpServerHandler handler;

        public Route(String action, String path, HttpServerHandler handler) {
            this.action = action;
            this.path = path;
            this.handler = handler;
        }

        public static Route of(String action, String path) {
            return new Route(action, path, null);
        }

        public static Route of(String action, String path, HttpServerHandler handler) {
            return new Route(action, path, handler);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Route route = (Route) o;
            return action.equals(route.action) &&
                    path.equals(route.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(action, path);
        }
    }

    public static class RouteBuilder {

        private final ServerBuilder serverBuilder;
        private final Set<Route> handlers;
        private final String context;

        public RouteBuilder(ServerBuilder builder, String context) {
            handlers = new HashSet<>();
            serverBuilder = builder;
            this.context = context;
        }

        public RouteBuilder get(String path, HttpServerHandler handler) {
            handlers.add(Route.of("get", path, handler));
            return this;
        }

        public RouteBuilder post(String path, HttpServerHandler handler) {
            handlers.add(Route.of("post", path, handler));
            return this;
        }

        public RouteBuilder patch(String path, HttpServerHandler handler) {
            handlers.add(Route.of("patch", path, handler));
            return this;
        }

        public RouteBuilder delete(String path, HttpServerHandler handler) {
            handlers.add(Route.of("delete", path, handler));
            return this;
        }

        public RouteBuilder action(String action, String path, HttpServerHandler handler) {
            handlers.add(Route.of(action, path, handler));
            return this;
        }

        public ServerBuilder build() {
            serverBuilder.add(context, handlers);
            return serverBuilder;
        }
    }
}
