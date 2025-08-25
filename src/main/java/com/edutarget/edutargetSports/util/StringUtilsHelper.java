package com.edutarget.edutargetSports.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtilsHelper {
    public static String capitalizeWords(String input) {
        if (input == null || input.isBlank()) return input;

        return Arrays.stream(input.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}