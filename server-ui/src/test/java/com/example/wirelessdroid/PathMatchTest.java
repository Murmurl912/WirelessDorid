package com.example.wirelessdroid;

import com.example.wirelessdroid.common.route.PathContainer;
import com.example.wirelessdroid.common.route.PathPattern;
import com.example.wirelessdroid.common.route.PathPatternParser;
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
