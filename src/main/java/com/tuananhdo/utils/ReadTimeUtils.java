package com.tuananhdo.utils;

import java.util.Arrays;

public class ReadTimeUtils {
    private static final int WORDS_PER_MINUTE = 200;
    private static final int ONE_MINUTE = 1;
    private static final String LESS_MIN_READ = "Less than 1 min";
    private static final String MIN_READ = " min read";

    public static String calculateReadTime(String content) {
        int wordCount = Arrays.stream(content.split("\\s+"))
                .mapToInt(String::length)
                .sum();
        int readTime = wordCount / WORDS_PER_MINUTE;
        return readTime < ONE_MINUTE ? LESS_MIN_READ : readTime + MIN_READ;
    }
}
