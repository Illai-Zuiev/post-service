package practical.post.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiErrorMessage {
    POST_NOT_FOUND_BY_ID("The post not found with id: %s"),
    USER_NOT_FOUND_BY_ID("The user not found with id: %s"),
    USER_NOT_FOUND_BY_EMAIL("The user not found with email: %s"),
    ROLE_NOT_FOUND_BY_NAME("The role not found with name: %s"),
    REFRESH_TOKEN_NOT_FOUND_BY_TOKEN("The refresh token not found with token: %s"),
    POST_WITH_THIS_TITLE_EXIST("The post with this title already exists: %s"),
    USER_WITH_THIS_USERNAME_EXIST("The user with this username already exists: %s"),
    USER_WITH_THIS_EMAIL_EXIST("The user with this email already exists: %s"),
    INVALID_EMAIL_OR_PASSWORD("The email or password is invalid"),
    MISMATCHED_PASSWORDS("The passwords should be the same"),
    INVALID_PASSWORD("The password is invalid and it should contain " +
            "at least " + ApiConstants.REQUIRED_MIN_CHARACTERS_NUMBER_IN_PASSWORD + " characters," +
            "at least " + ApiConstants.REQUIRED_MIN_DIGITS_NUMBER_IN_PASSWORD + " digits," +
            "at least " + ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD + " letters in every case," +
            "and has length " + ApiConstants.REQUIRED_MIN_PASSWORD_LENGTH + " symbols"),
    ACCESS_DENIED("You don't have necessary permissions"),
    EXPIRED_TOKEN("Token is expired"),
    INVALID_TOKEN_SIGNATURE("Token has invalid signature"),
    ERROR_DURING_JWT_PROCESSING("Error during jwt processing"),
    ;
    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
