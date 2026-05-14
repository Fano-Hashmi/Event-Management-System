package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Input validation utilities and password hashing.
 */
public class Validation {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DATE_PATTERN =
        Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN =
        Pattern.compile("^\\d{2}:\\d{2}$");

    /**
     * Hash a password using SHA-256.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidDate(String date) {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }

    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time).matches();
    }

    public static boolean isPositiveNumber(String str) {
        try {
            return Double.parseDouble(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String str) {
        try {
            return Integer.parseInt(str) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }
}
