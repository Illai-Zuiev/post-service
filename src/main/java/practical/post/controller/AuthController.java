package practical.post.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practical.post.model.constants.ApiLogMessage;
import practical.post.model.dto.user.UserProfileDto;
import practical.post.model.request.auth.LoginRequest;
import practical.post.model.request.auth.RegistrationRequest;
import practical.post.model.response.CustomResponse;
import practical.post.service.AuthService;
import practical.post.utils.ApiUtils;

@RequestMapping("${end.point.auth}")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("${end.point.login}")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<UserProfileDto> loginResponse = authService.login(loginRequest);
        Cookie cookie = ApiUtils.createAuthCookie(loginResponse.getBody().getToken());
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("${end.point.registration}")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationRequest registrationRequest, HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<UserProfileDto> registrationResponse = authService.registration(registrationRequest);
        Cookie cookie = ApiUtils.createAuthCookie(registrationResponse.getBody().getToken());
        response.addCookie(cookie);

        return ResponseEntity.ok(registrationResponse);
    }

    @GetMapping("${end.point.refresh_token}")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken,HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<UserProfileDto> refreshResponse = authService.refreshToken(refreshToken);
        Cookie cookie = ApiUtils.createAuthCookie(refreshResponse.getBody().getToken());
        response.addCookie(cookie);

        return ResponseEntity.ok(refreshResponse);
    }
}
