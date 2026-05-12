package practical.post.model.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest implements Serializable {
    @NotBlank(message = "Title should be present")
    @Size(min = 5, max = 50, message = "Title should be from 5 to 50 symbols")
    private String title;
    @NotBlank(message = "Content should be present")
    @Size(min = 10, max = 2000, message = "Content should be from 10 to 2000 symbols")
    private String content;
}
