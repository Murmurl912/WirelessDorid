package com.example.httpserver.app.services.http.route;

import java.util.function.BiFunction;

public class RouteBuilder {
    private final PathPatternParser DEFAULT_PATH_PATTERN_PARSER = new PathPatternParser();

    public RouteBuilder() {

    }

    public <T, U, R> RouteBuilder GET(String url, BiFunction<T, U, R> handler) {
        PathPattern pattern = pattern(url);

        return this;
    };


    private PathPattern pattern(String url) {
        return DEFAULT_PATH_PATTERN_PARSER.parse(url);
    }

}
