package practical.post.model.request.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.enums.CommentSortField;
import practical.post.model.enums.PostSortField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSearchRequest implements Serializable {
    private Integer postId;
    private String content;
    private Boolean deleted;
    private String keyword;
    private CommentSortField commentSortField;
}
