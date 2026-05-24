package practical.post.service;

import practical.post.model.dto.user.UserProfileDto;
import practical.post.model.request.auth.LoginRequest;
import practical.post.model.request.auth.RegistrationRequest;
import practical.post.model.response.CustomResponse;

public interface AuthService {
    CustomResponse<UserProfileDto> login(LoginRequest loginRequest);

    CustomResponse<UserProfileDto> registration(RegistrationRequest registrationRequest);

    CustomResponse<UserProfileDto> refreshToken(String refreshToken);
}
