package com.springcloud.zuul.apigateway.utils;

import org.springframework.util.AntPathMatcher;

public class PathMatchUtil {
    private static AntPathMatcher matcher = new AntPathMatcher();

    public static boolean isPathMatch(String pattern, String path) {
        return matcher.match(pattern, path);
    }
}
