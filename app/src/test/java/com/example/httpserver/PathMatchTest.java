package com.example.httpserver;

import com.example.httpserver.common.route.PathContainer;
import com.example.httpserver.common.route.PathPattern;
import com.example.httpserver.common.route.PathPatternParser;
import org.junit.Test;

public class PathMatchTest {

    @Test
    public void test() {
        String url = "/{context}/{*path}";

        PathPatternParser pathPatternParser = new PathPatternParser();
        PathPattern pathPattern = pathPatternParser.parse(url);

        PathContainer pathContainer = PathContainer.parsePath("/");

        PathPattern.PathMatchInfo info = pathPattern.matchAndExtract(pathContainer);

        if(info != null)
            System.out.println(info.getUriVariables());
    }

}
