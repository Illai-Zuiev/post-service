package practical.post.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.enums.RegistrationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    private int id;
    private String email;
    private String username;
    private LocalDateTime created;
    private LocalDateTime updated;
    private RegistrationStatus registrationStatus;
    private LocalDateTime lastLogin;
}
