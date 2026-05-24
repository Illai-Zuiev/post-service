package practical.post.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import practical.post.model.constants.ApiMessage;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T> implements Serializable {
    private String message;
    private T body;
    private boolean success;

    public static <T extends Serializable> CustomResponse<T> createSuccessful(T body) {
        return new CustomResponse<>(StringUtils.EMPTY, body, true);
    }

    public static <T extends Serializable> CustomResponse<T> createSuccessfulWithNewToken(T body) {
        return new CustomResponse<>(ApiMessage.TOKEN_CREATED_OR_UPDATED.getMessage(), body, true);
    }
}
