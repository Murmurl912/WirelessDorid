package com.example.httpserver;

import com.example.httpserver.app.services.route.PathContainer;
import com.example.httpserver.app.services.route.PathPattern;
import com.example.httpserver.app.services.route.PathPatternParser;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PathMatchTest {

    @Test
    public void test() {
        String url = "/{context}/{*path}";

        PathPatternParser pathPatternParser = new PathPatternParser();
        PathPattern pathPattern = pathPatternParser.parse(url);

        PathContainer pathContainer = PathContainer.parsePath("/api/hello/world");

        PathPattern.PathMatchInfo info = pathPattern.matchAndExtract(pathContainer);

        if(info != null)
            System.out.println(info.getUriVariables());
    }

}
