package practical.post.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDto implements Serializable {
    private int id;
    private String email;
    private String username;
    private LocalDateTime created;
    private LocalDateTime updated;
    private boolean deleted;
}
