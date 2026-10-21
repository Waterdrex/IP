package com.tcet.pms.util;

/**
 * Utility class providing static methods for input validation.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static void requireNotEmpty(String str, String fieldName) {
        if (!isNotEmpty(str)) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty.");
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
