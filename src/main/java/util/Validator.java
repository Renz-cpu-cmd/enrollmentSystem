package util;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^(09|\\+639)\\d{9}$");

    /**
     * Validates an email address against a standard email pattern.
     *
     * @param email The email address to validate.
     * @return true if the email is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates a Philippine mobile number.
     * It accepts formats like 09xxxxxxxxx or +639xxxxxxxxx.
     *
     * @param number The mobile number to validate.
     * @return true if the number is valid, false otherwise.
     */
    public static boolean isValidPhilippineMobileNumber(String number) {
        if (number == null) {
            return false;
        }
        return MOBILE_NUMBER_PATTERN.matcher(number).matches();
    }

    /**
     * Checks if one or more strings are null or empty (after trimming).
     *
     * @param fields A variable number of string fields to check.
     * @return true if ALL fields are not null and not empty, false otherwise.
     */
    public static boolean isNotNullOrEmpty(String... fields) {
        if (fields == null) {
            return false;
        }
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
