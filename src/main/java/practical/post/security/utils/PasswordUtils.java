package practical.post.security.utils;

import practical.post.model.constants.ApiConstants;

public class PasswordUtils {
    public static boolean isNotValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return true;
        }

        String trimPassword = password.trim();

        if (trimPassword.length() < ApiConstants.REQUIRED_MIN_PASSWORD_LENGTH) {
            return true;
        }

        int charactersNumber = ApiConstants.REQUIRED_MIN_CHARACTERS_NUMBER_IN_PASSWORD;
        int lettersLowerCase = ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD;
        int lettersUpperCase = ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD;
        int digits = ApiConstants.REQUIRED_MIN_DIGITS_NUMBER_IN_PASSWORD;

        for (int i = 0; i < trimPassword.length(); i++) {
            String currentLetter = String.valueOf(trimPassword.charAt(i));

            if (!ApiConstants.PASSWORD_ALLCHARACTERS.contains(currentLetter)) {
                return true;
            }

            if (ApiConstants.PASSWORD_DIGITS.contains(currentLetter)) {
                digits--;
            }

            if (ApiConstants.PASSWORD_LETTERS_LOWER_CASE.contains(currentLetter)) {
                lettersLowerCase--;
            }

            if (ApiConstants.PASSWORD_LETTERS_UPPER_CASE.contains(currentLetter)) {
                lettersUpperCase--;
            }

            if (ApiConstants.PASSWORDCHARACTERS.contains(currentLetter)) {
                charactersNumber--;
            }
        }

        return charactersNumber > 0 || lettersLowerCase > 0 || lettersUpperCase > 0 || digits > 0;
    }
}
