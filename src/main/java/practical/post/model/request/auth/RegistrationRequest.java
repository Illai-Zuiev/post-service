package practical.post.model.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest implements Serializable {
    @NotBlank(message = "Username cant be blank")
    private String username;
    @NotBlank(message = "Email cant be blank")
    private String email;
    @NotBlank(message = "Password cant be blank")
    private String password;
    @NotBlank(message = "Confirm password cant be blank")
    private String confirmPassword;
}
