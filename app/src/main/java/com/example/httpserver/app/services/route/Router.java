package com.example.httpserver.app.services.route;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Router<Handler> {

    private final Set<Route<Handler>> routes;

    public Router() {
        routes = new HashSet<>();
    }

    public synchronized Route<Handler> route(String method, String uri, Map<String, String> pathVariables) {
        Map<Route<Handler>, PathPattern.PathMatchInfo> matches = new ConcurrentHashMap<>();

        List<Route<Handler>> list = routes.stream().filter(route -> route.method.matches(method))
                .filter(route -> {
                    PathPattern.PathMatchInfo info = route.predicate(uri);
                    if(info == null) {
                        return false;
                    }
                    matches.put(route, info);
                    return true;
                }).sorted().collect(Collectors.toList());
        if(list.isEmpty()) {
            return notFound();
        }

        Route<Handler> route = list.get(0);
        PathPattern.PathMatchInfo info = matches.get(route);
        if(info != null)
            pathVariables.putAll(info.getUriVariables());
        return route;
    }

    public Router<Handler> add(Route<Handler> route) {
        routes.add(route);
        return this;
    }

    public void remove(Route<Handler> route) {
        routes.remove(route);
    }

    public void clear() {
        routes.clear();
    }

    private Route<Handler> notFound() {
        return null;
    }

    public static class Route<Handler> implements Comparable<Route<Handler>> {

        public final HttpMethod method;
        public final PathPattern pattern;
        public final Handler handler;
        private static final PathPatternParser DEFAULT_PATH_PARSER = new PathPatternParser();

        public Route(Handler handler,
                     HttpMethod method,
                     PathPattern pattern) {
            this.handler = handler;
            this.method = method;
            this.pattern = pattern;
        }

        public PathPattern.PathMatchInfo predicate(String uri) {
            return pattern.matchAndExtract(PathContainer.parsePath(uri));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Route<?> route = (Route<?>) o;
            return method == route.method &&
                    Objects.equals(pattern, route.pattern);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, pattern);
        }

        public static <T> Route<T> of(String method, String uri, T handler) {
            HttpMethod m = HttpMethod.resolve(method);
            PathPattern p = DEFAULT_PATH_PARSER.parse(uri);

            return new Route<>(handler, m, p);
        }

        @Override
        public int compareTo(Route<Handler> o) {
            return pattern.compareTo(o.pattern);
        }
    }
}
