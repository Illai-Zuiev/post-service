package practical.post.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchDto implements Serializable {
    private int id;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime updated;
    private boolean deleted;
}
