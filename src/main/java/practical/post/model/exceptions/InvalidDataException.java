package practical.post.model.exceptions;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
