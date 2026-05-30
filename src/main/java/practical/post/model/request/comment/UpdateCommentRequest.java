package practical.post.model.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequest implements Serializable {
    @NotBlank(message = "Content should be present")
    @Size(min = 10, max = 2000, message = "Content should be from 10 to 2000 symbols")
    private String content;
}
