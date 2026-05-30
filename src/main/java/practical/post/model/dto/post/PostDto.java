package practical.post.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.dto.user.OwnerDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto implements Serializable {
    private int id;
    private OwnerDto owner;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime updated;
}
