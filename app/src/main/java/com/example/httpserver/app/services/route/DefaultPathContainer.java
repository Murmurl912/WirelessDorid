package com.example.httpserver.app.services.route;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class DefaultPathContainer implements PathContainer {
    private static final MultiValueMap<String, String> EMPTY_PARAMS = new LinkedMultiValueMap();
    private static final PathContainer EMPTY_PATH = new DefaultPathContainer("", Collections.emptyList());
    private static final Map<Character, DefaultSeparator> SEPARATORS = new HashMap(2);
    private final String path;
    private final List<Element> elements;

    private DefaultPathContainer(String path, List<Element> elements) {
        this.path = path;
        this.elements = Collections.unmodifiableList(elements);
    }

    public String value() {
        return this.path;
    }

    public List<Element> elements() {
        return this.elements;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return !(other instanceof PathContainer) ? false : this.value().equals(((PathContainer)other).value());
        }
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return this.value();
    }

    static PathContainer createFromUrlPath(String path, Options options) {
        if (path.isEmpty()) {
            return EMPTY_PATH;
        } else {
            char separator = options.separator();
            DefaultSeparator separatorElement = (DefaultSeparator)SEPARATORS.get(separator);
            if (separatorElement == null) {
                throw new IllegalArgumentException("Unexpected separator: '" + separator + "'");
            } else {
                List<Element> elements = new ArrayList();
                int begin;
                if (path.charAt(0) == separator) {
                    begin = 1;
                    elements.add(separatorElement);
                } else {
                    begin = 0;
                }

                while(begin < path.length()) {
                    int end = path.indexOf(separator, begin);
                    String segment = end != -1 ? path.substring(begin, end) : path.substring(begin);
                    if (!segment.isEmpty()) {
                        elements.add(options.shouldDecodeAndParseSegments() ? decodeAndParsePathSegment(segment) : new DefaultPathSegment(segment, separatorElement));
                    }

                    if (end == -1) {
                        break;
                    }

                    elements.add(separatorElement);
                    begin = end + 1;
                }

                return new DefaultPathContainer(path, elements);
            }
        }
    }

    private static PathSegment decodeAndParsePathSegment(String segment) {
        Charset charset = StandardCharsets.UTF_8;
        int index = segment.indexOf(59);
        String valueToMatch;
        if (index == -1) {
            valueToMatch = StringUtils.uriDecode(segment, charset);
            return new DefaultPathSegment(segment, valueToMatch, EMPTY_PARAMS);
        } else {
            valueToMatch = StringUtils.uriDecode(segment.substring(0, index), charset);
            String pathParameterContent = segment.substring(index);
            MultiValueMap<String, String> parameters = parsePathParams(pathParameterContent, charset);
            return new DefaultPathSegment(segment, valueToMatch, parameters);
        }
    }

    private static MultiValueMap<String, String> parsePathParams(String input, Charset charset) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap();

        int end;
        for(int begin = 1; begin < input.length(); begin = end + 1) {
            end = input.indexOf(59, begin);
            String param = end != -1 ? input.substring(begin, end) : input.substring(begin);
            parsePathParamValues(param, charset, result);
            if (end == -1) {
                break;
            }
        }

        return result;
    }

    private static void parsePathParamValues(String input, Charset charset, MultiValueMap<String, String> output) {
        if (StringUtils.hasText(input)) {
            int index = input.indexOf(61);
            String name;
            if (index != -1) {
                name = input.substring(0, index);
                String value = input.substring(index + 1);
                String[] var6 = StringUtils.commaDelimitedListToStringArray(value);
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    String v = var6[var8];
                    name = StringUtils.uriDecode(name, charset);
                    if (StringUtils.hasText(name)) {
                        output.add(name, StringUtils.uriDecode(v, charset));
                    }
                }
            } else {
                name = StringUtils.uriDecode(input, charset);
                if (StringUtils.hasText(name)) {
                    output.add(input, "");
                }
            }
        }

    }

    static PathContainer subPath(PathContainer container, int fromIndex, int toIndex) {
        List<Element> elements = container.elements();
        if (fromIndex == 0 && toIndex == elements.size()) {
            return container;
        } else if (fromIndex == toIndex) {
            return EMPTY_PATH;
        } else {
            Assert.isTrue(fromIndex >= 0 && fromIndex < elements.size(), () -> {
                return "Invalid fromIndex: " + fromIndex;
            });
            Assert.isTrue(toIndex >= 0 && toIndex <= elements.size(), () -> {
                return "Invalid toIndex: " + toIndex;
            });
            Assert.isTrue(fromIndex < toIndex, () -> {
                return "fromIndex: " + fromIndex + " should be < toIndex " + toIndex;
            });
            List<Element> subList = elements.subList(fromIndex, toIndex);
            String path = (String)subList.stream().map(Element::value).collect(Collectors.joining(""));
            return new DefaultPathContainer(path, subList);
        }
    }

    static {
        SEPARATORS.put('/', new DefaultSeparator('/', "%2F"));
        SEPARATORS.put('.', new DefaultSeparator('.', "%2E"));
    }

    private static class DefaultPathSegment implements PathSegment {
        private final String value;
        private final String valueToMatch;
        private final char[] valueToMatchAsChars;
        private final MultiValueMap<String, String> parameters;

        DefaultPathSegment(String value, String valueToMatch, MultiValueMap<String, String> params) {
            this.value = value;
            this.valueToMatch = valueToMatch;
            this.valueToMatchAsChars = valueToMatch.toCharArray();
            this.parameters = CollectionUtils.unmodifiableMultiValueMap(params);
        }

        DefaultPathSegment(String value, DefaultSeparator separator) {
            this.value = value;
            this.valueToMatch = value.contains(separator.encodedSequence()) ? value.replaceAll(separator.encodedSequence(), separator.value()) : value;
            this.valueToMatchAsChars = this.valueToMatch.toCharArray();
            this.parameters = DefaultPathContainer.EMPTY_PARAMS;
        }

        public String value() {
            return this.value;
        }

        public String valueToMatch() {
            return this.valueToMatch;
        }

        public char[] valueToMatchAsChars() {
            return this.valueToMatchAsChars;
        }

        public MultiValueMap<String, String> parameters() {
            return this.parameters;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else {
                return !(other instanceof PathSegment) ? false : this.value().equals(((PathSegment)other).value());
            }
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return "[value='" + this.value + "']";
        }
    }

    private static class DefaultSeparator implements Separator {
        private final String separator;
        private final String encodedSequence;

        DefaultSeparator(char separator, String encodedSequence) {
            this.separator = String.valueOf(separator);
            this.encodedSequence = encodedSequence;
        }

        public String value() {
            return this.separator;
        }

        public String encodedSequence() {
            return this.encodedSequence;
        }
    }
}
