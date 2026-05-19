package practical.post.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiErrorMessage {
    POST_NOT_FOUND_BY_ID("The post not found with id: %s"),
    USER_NOT_FOUND_BY_ID("The user not found with id: %s"),
    POST_WITH_THIS_TITLE_EXIST("The post with this title already exists: %s"),
    USER_WITH_THIS_USERNAME_EXIST("The user with this username already exists: %s"),
    USER_WITH_THIS_EMAIL_EXIST("The user with this email already exists: %s"),

    ;
    private final String message;

    public String getMessage(Object... args){
        return String.format(message, args);
    }
}
