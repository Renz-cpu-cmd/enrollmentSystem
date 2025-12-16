package util;

import java.util.regex.Pattern;

/**
 * Centralized validation utility for user input and business rules.
 * Add new validation methods as needed for the enrollment system.
 */
public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^(09|\\+639)\\d{9}$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[A-Z]{2}\\d{4}$"); // e.g., AB1234

    /** Validates an email address. */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /** Validates a Philippine mobile number (09xxxxxxxxx or +639xxxxxxxxx). */
    public static boolean isValidPhilippineMobileNumber(String number) {
        return number != null && MOBILE_NUMBER_PATTERN.matcher(number).matches();
    }

    /** Validates a student ID (e.g., AB1234). */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    /** Checks if all fields are not null and not empty after trimming. */
    public static boolean isNotEmpty(String... fields) {
        if (fields == null) return false;
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) return false;
        }
        return true;
    }

    /** Validates password (minimum 8 characters). */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    // Add more validation methods as needed
}