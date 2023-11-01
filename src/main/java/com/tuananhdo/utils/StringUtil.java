package com.tuananhdo.utils;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class StringUtil {
    public static String getUrl(String postTitle) {
        String title = postTitle.toLowerCase();
        title = Normalizer.normalize(title, Normalizer.Form.NFD);
        title = title.trim();
        title = title.replaceAll("\\s+", "-");
        title = title.replaceAll("đ", "d");
        return title;
    }
    public static String truncateString(String input, int maxLength) {
        if (input.length() > maxLength) {
            return input.substring(0, maxLength) + "...";
        } else {
            return input;
        }
    }
    public static String toLowerCase(String input) {
        return "\" " + input.toLowerCase() + "\"";
    }
}
