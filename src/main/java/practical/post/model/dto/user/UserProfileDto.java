package practical.post.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.dto.role.RoleDto;
import practical.post.model.enums.RegistrationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto implements Serializable {
    private int id;
    private String email;
    private String username;
    private RegistrationStatus registrationStatus;
    private LocalDateTime lastLogin;
    private String token;
    private String refreshToken;
    private List<RoleDto> roles;
}
