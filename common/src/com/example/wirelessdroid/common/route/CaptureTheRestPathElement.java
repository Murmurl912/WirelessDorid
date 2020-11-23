package com.example.wirelessdroid.common.route;


import java.util.List;

/**
 * A path element representing capturing the rest of a path. In the pattern
 * '/foo/{*foobar}' the /{*foobar} is represented as a {@link CaptureTheRestPathElement}.
 *
 * @author Andy Clement
 * @since 5.0
 */
class CaptureTheRestPathElement extends PathElement {

    private final String variableName;


    /**
     * Create a new {@link CaptureTheRestPathElement} instance.
     *
     * @param pos               position of the path element within the path pattern text
     * @param captureDescriptor a character array containing contents like '{' '*' 'a' 'b' '}'
     * @param separator         the separator used in the path pattern
     */
    CaptureTheRestPathElement(int pos, char[] captureDescriptor, char separator) {
        super(pos, separator);
        this.variableName = new String(captureDescriptor, 2, captureDescriptor.length - 3);
    }

    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        // No need to handle 'match start' checking as this captures everything
        // anyway and cannot be followed by anything else
        // assert next == null

        // If there is more data, it must start with the separator
        if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
            return false;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = matchingContext.pathLength;
        }
        if (matchingContext.extractingVariables) {
            // Collect the parameters from all the remaining segments
            MultiValueMap<String, String> parametersCollector = null;
            for (int i = pathIndex; i < matchingContext.pathLength; i++) {
                PathContainer.Element element = matchingContext.pathElements.get(i);
                if (element instanceof PathContainer.PathSegment) {
                    MultiValueMap<String, String> parameters = ((PathContainer.PathSegment) element).parameters();
                    if (!parameters.isEmpty()) {
                        if (parametersCollector == null) {
                            parametersCollector = new LinkedMultiValueMap<>();
                        }
                        parametersCollector.addAll(parameters);
                    }
                }
            }
            matchingContext.set(this.variableName, pathToString(pathIndex, matchingContext.pathElements),
                    parametersCollector == null ? NO_PARAMETERS : parametersCollector);
        }
        return true;
    }

    private String pathToString(int fromSegment, List<PathContainer.Element> pathElements) {
        StringBuilder buf = new StringBuilder();
        for (int i = fromSegment, max = pathElements.size(); i < max; i++) {
            PathContainer.Element element = pathElements.get(i);
            if (element instanceof PathContainer.PathSegment) {
                buf.append(((PathContainer.PathSegment) element).valueToMatch());
            } else {
                buf.append(element.value());
            }
        }
        return buf.toString();
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public int getWildcardCount() {
        return 0;
    }

    @Override
    public int getCaptureCount() {
        return 1;
    }


    @Override
    public String toString() {
        return "CaptureTheRest(/{*" + this.variableName + "})";
    }

    @Override
    public char[] getChars() {
        return ("/{*" + this.variableName + "}").toCharArray();
    }
}
