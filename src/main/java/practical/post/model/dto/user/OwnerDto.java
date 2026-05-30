package practical.post.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto implements Serializable {
    private int id;
    private String email;
    private String username;
}
