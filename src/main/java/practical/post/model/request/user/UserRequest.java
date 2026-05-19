package practical.post.model.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest implements Serializable {
    @NotBlank(message = "Email should be present")
    @Size(min = 10, max = 100, message = "Email should be from 5 to 100 symbols")
    private String email;
    @NotBlank(message = "Username should be present")
    @Size(min = 1, max = 100, message = "Username should be from 5 to 100 symbols")
    private String username;
    @NotBlank(message = "Password should be present")
    @Size(min = 5, max = 100, message = "Password should be from 5 to 100 symbols")
    private String password;
}
