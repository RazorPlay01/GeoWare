package com.github.razorplay01.geoware.geowarecommon.util;

public class UtilParser {
    private UtilParser() {
        //[]
    }

    public static int parseInteger(String input, String errorMessage) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static double parseDouble(String input, String errorMessage) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static <T extends Enum<T>> T parseEnum(String input, Class<T> enumClass, String errorMessage) {
        try {
            return Enum.valueOf(enumClass, input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static boolean parseBoolean(String input, String errorMessage) {
        if ("true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input)) {
            return Boolean.parseBoolean(input);
        } else {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
