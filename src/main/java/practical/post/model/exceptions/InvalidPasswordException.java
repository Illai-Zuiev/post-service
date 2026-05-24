package practical.post.model.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
