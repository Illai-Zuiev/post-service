package practical.post.model.request.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.enums.PostSortField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchRequest implements Serializable {
    private String title;
    private String content;
    private Boolean deleted;
    private String keyword;
    private PostSortField postSortField;
}
