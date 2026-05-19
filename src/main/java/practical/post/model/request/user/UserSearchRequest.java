package practical.post.model.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.enums.UserSortField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest implements Serializable {
    private String email;
    private String username;
    private Boolean deleted;
    private String keyword;
    private UserSortField userSortField;
}
