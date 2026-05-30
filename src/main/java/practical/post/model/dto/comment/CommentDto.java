package practical.post.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.dto.user.OwnerDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {
    private int id;
    private OwnerDto owner;
    private int postId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime updated;
}
