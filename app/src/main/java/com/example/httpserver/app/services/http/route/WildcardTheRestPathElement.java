package com.example.httpserver.app.services.http.route;

/**
 * A path element representing wildcarding the rest of a path. In the pattern
 * '/foo/**' the /** is represented as a {@link WildcardTheRestPathElement}.
 *
 * @author Andy Clement
 * @since 5.0
 */
class WildcardTheRestPathElement extends PathElement {

    WildcardTheRestPathElement(int pos, char separator) {
        super(pos, separator);
    }


    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        // If there is more data, it must start with the separator
        if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
            return false;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = matchingContext.pathLength;
        }
        return true;
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public int getWildcardCount() {
        return 1;
    }


    @Override
    public String toString() {
        return "WildcardTheRest(" + this.separator + "**)";
    }

    @Override
    public char[] getChars() {
        return (this.separator+"**").toCharArray();
    }
}
