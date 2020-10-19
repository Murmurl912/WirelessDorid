package com.example.httpserver.common.route;

public class PathRequestPredicate implements RequestPredicate {

    public final PathPattern pattern;
    private PathPattern.PathMatchInfo info;

    public PathRequestPredicate(PathPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public <T> boolean predicate(T value) {
        String uri = value.toString();
        PathContainer pathContainer = PathContainer.parsePath(uri);
        PathPattern.PathMatchInfo info = pattern.matchAndExtract(pathContainer);
        return info != null;
    }

    public PathPattern.PathMatchInfo getInfo() {
        return info;
    }

}
