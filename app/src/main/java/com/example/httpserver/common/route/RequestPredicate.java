package com.example.httpserver.common.route;

import java.util.Objects;

public interface RequestPredicate {

    public <T> boolean predicate(T value);

    default RequestPredicate and(RequestPredicate other) {
        return new AndRequestPredicate(this, other);
    }

    default RequestPredicate or(RequestPredicate other) {
        return new OrRequestPredicate(this, other);
    }

    public static class AndRequestPredicate implements RequestPredicate {
        private final RequestPredicate left;
        private final RequestPredicate right;

        public AndRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
            this.left = left;
            this.right = right;
        }

        @Override
        public <T> boolean predicate(T value) {
            return this.left.predicate(value) && this.right.predicate(value);
        }
    }

    public static class OrRequestPredicate implements RequestPredicate {
        private final RequestPredicate left;
        private final RequestPredicate right;

        public OrRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
            this.left = left;
            this.right = right;
        }

        @Override
        public <T> boolean predicate(T value) {
            return this.left.predicate(value) || this.right.predicate(value);
        }
    }
}
