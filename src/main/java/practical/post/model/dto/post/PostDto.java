package practical.post.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class PostDto implements Serializable {
    private int id;
    private String title;
    private String content;
    private LocalDateTime created = LocalDateTime.now();
}
