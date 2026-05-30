package practical.post.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import practical.post.mapper.UserMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.user.UserProfileDto;
import practical.post.model.entity.RefreshToken;
import practical.post.model.entity.Role;
import practical.post.model.entity.User;
import practical.post.model.enums.UserSystemRole;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.InvalidDataException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.auth.LoginRequest;
import practical.post.model.request.auth.RegistrationRequest;
import practical.post.model.response.CustomResponse;
import practical.post.repository.RoleRepository;
import practical.post.repository.UserRepository;
import practical.post.security.JwtTokenProvider;
import practical.post.security.utils.PasswordUtils;
import practical.post.service.AuthService;
import practical.post.service.RefreshTokenService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessValidationService accessValidationService;

    @Override
    public CustomResponse<UserProfileDto> login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidDataException(ApiErrorMessage.INVALID_EMAIL_OR_PASSWORD.getMessage());
        }

        User user = userRepository.findByEmailAndDeletedFalse(loginRequest.getEmail()).orElseThrow(
                () -> new InvalidDataException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(loginRequest.getEmail()))
        );

        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);
        String token = jwtTokenProvider.generateToken(user);
        UserProfileDto userProfileDto = userMapper.createUserProfileDto(user, token, refreshToken.getToken());

        return CustomResponse.createSuccessfulWithNewToken(userProfileDto);
    }

    @Override
    public CustomResponse<UserProfileDto> registration(RegistrationRequest registrationRequest) {
        accessValidationService.validateUserBeforeRegistration(registrationRequest);

        User user = userMapper.convertUserRequestToUser(registrationRequest);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(UserSystemRole.USER.getRole()).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.ROLE_NOT_FOUND_BY_NAME.getMessage(UserSystemRole.USER.getRole()))
        );
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user = userRepository.save(user);

        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);
        String token = jwtTokenProvider.generateToken(user);
        UserProfileDto userProfileDto = userMapper.createUserProfileDto(user, token, refreshToken.getToken());

        return CustomResponse.createSuccessfulWithNewToken(userProfileDto);
    }

    @Override
    public CustomResponse<UserProfileDto> refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.validateAndRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();
        String token = jwtTokenProvider.generateToken(user);
        UserProfileDto userProfileDto = userMapper.createUserProfileDto(user, token, refreshToken.getToken());

        return CustomResponse.createSuccessfulWithNewToken(userProfileDto);
    }
}
