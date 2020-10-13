package com.example.httpserver.app.services.route;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MethodRequestPredicate implements RequestPredicate {

    private final Set<HttpMethod> methods;
    private HttpMethod method;

    public MethodRequestPredicate(HttpMethod method) {
        methods = new HashSet<>();
        methods.add(method);
    }

    public MethodRequestPredicate(HttpMethod...methods) {
        this.methods = new HashSet<>(Arrays.asList(methods));
    }

    @Override
    public <T> boolean predicate(T value) {
        method = HttpMethod.resolve(value.toString());
        return methods.contains(method);
    }

    public HttpMethod getMethod() {
        return method;
    }
}
