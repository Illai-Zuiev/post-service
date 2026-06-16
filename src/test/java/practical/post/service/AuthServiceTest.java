package practical.post.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import practical.post.mapper.UserMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.user.UserProfileDto;
import practical.post.model.entity.RefreshToken;
import practical.post.model.entity.Role;
import practical.post.model.entity.User;
import practical.post.model.enums.RegistrationStatus;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.InvalidDataException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.auth.LoginRequest;
import practical.post.model.request.auth.RegistrationRequest;
import practical.post.model.response.CustomResponse;
import practical.post.repository.RoleRepository;
import practical.post.repository.UserRepository;
import practical.post.security.JwtTokenProvider;
import practical.post.service.impl.AccessValidationService;
import practical.post.service.impl.AuthServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AccessValidationService accessValidationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserProfileDto testUserProfileDto;
    private RefreshToken testRefreshToken;
    private Role userRole;

    @BeforeEach
    void beforeEach() {
        userRole = new Role();
        userRole.setName("USER");

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testusername");
        testUser.setEmail("testemail");
        testUser.setPassword("hashedpassword");
        testUser.setRegistrationStatus(RegistrationStatus.ACTIVE);
        testUser.setLastLogin(LocalDateTime.now());
        testUser.setRoles(Collections.singletonList(userRole));

        testRefreshToken = new RefreshToken();
        testRefreshToken.setToken("testrefreshtoken");
        testRefreshToken.setUser(testUser);

        testUserProfileDto = new UserProfileDto(
                testUser.getId(),
                testUser.getEmail(),
                testUser.getUsername(),
                testUser.getRegistrationStatus(),
                testUser.getLastLogin(),
                "token",
                testRefreshToken.getToken(),
                Collections.emptyList()
        );
    }

    @Test
    void login_Success_CustomResponseWithUserProfileDto() {
        LoginRequest loginRequest = new LoginRequest("testemail", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmailAndDeletedFalse(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(refreshTokenService.generateOrUpdateRefreshToken(testUser))
                .thenReturn(testRefreshToken);
        when(jwtTokenProvider.generateToken(testUser))
                .thenReturn(testUserProfileDto.getToken());
        when(userMapper.createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken()))
                .thenReturn(testUserProfileDto);

        CustomResponse<UserProfileDto> response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(testUserProfileDto, response.getBody());

        verify(authenticationManager).authenticate((any(UsernamePasswordAuthenticationToken.class)));
        verify(userRepository).findByEmailAndDeletedFalse(loginRequest.getEmail());
        verify(refreshTokenService).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider).generateToken(testUser);
        verify(userMapper).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void login_InvalidEmailOrPassword_ThrownInvalidDataException() {
        LoginRequest loginRequest = new LoginRequest("testemail", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new InvalidDataException(ApiErrorMessage.INVALID_EMAIL_OR_PASSWORD.getMessage()));

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(
                ApiErrorMessage.INVALID_EMAIL_OR_PASSWORD.getMessage(),
                exception.getMessage()
        );

        verify(authenticationManager).authenticate((any(UsernamePasswordAuthenticationToken.class)));
        verify(userRepository, never()).findByEmailAndDeletedFalse(loginRequest.getEmail());
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void login_UserNotFoundByEmail_ThrownInvalidDataException() {
        LoginRequest loginRequest = new LoginRequest("testemail", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmailAndDeletedFalse(loginRequest.getEmail()))
                .thenThrow(new InvalidDataException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(loginRequest.getEmail())));

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(
                ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(loginRequest.getEmail()),
                exception.getMessage()
        );

        verify(authenticationManager).authenticate((any(UsernamePasswordAuthenticationToken.class)));
        verify(userRepository).findByEmailAndDeletedFalse(loginRequest.getEmail());
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void registration_Success_CustomResponseWithUserProfileDto() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "testusername",
                "testemail",
                "password",
                "password"
        );

        doNothing().when(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        when(userMapper.convertUserRequestToUser(registrationRequest))
                .thenReturn(testUser);
        when(roleRepository.findByName(userRole.getName()))
                .thenReturn(Optional.of(userRole));
        when(userRepository.save(testUser))
                .thenReturn(testUser);
        when(refreshTokenService.generateOrUpdateRefreshToken(testUser))
                .thenReturn(testRefreshToken);
        when(jwtTokenProvider.generateToken(testUser))
                .thenReturn(testUserProfileDto.getToken());
        when(userMapper.createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken()))
                .thenReturn(testUserProfileDto);

        CustomResponse<UserProfileDto> response = authService.registration(registrationRequest);

        assertNotNull(response);
        assertEquals(testUserProfileDto, response.getBody());

        verify(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        verify(userMapper).convertUserRequestToUser(registrationRequest);
        verify(roleRepository).findByName(userRole.getName());
        verify(userRepository).save(testUser);
        verify(refreshTokenService).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider).generateToken(testUser);
        verify(userMapper).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void registration_MismatchedPassword_ThrownInvalidDataException() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "testusername",
                "testemail",
                "password",
                "password"
        );

        doThrow(new InvalidDataException(ApiErrorMessage.MISMATCHED_PASSWORDS.getMessage())).when(accessValidationService).validateUserBeforeRegistration(registrationRequest);

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> authService.registration(registrationRequest)
        );

        assertEquals(
                ApiErrorMessage.MISMATCHED_PASSWORDS.getMessage(),
                exception.getMessage()
        );

        verify(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        verify(userMapper, never()).convertUserRequestToUser(registrationRequest);
        verify(roleRepository, never()).findByName(userRole.getName());
        verify(userRepository, never()).save(testUser);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void registration_InvalidPassword_ThrownInvalidDataException() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "testusername",
                "testemail",
                "password",
                "password"
        );

        doThrow(new InvalidDataException(ApiErrorMessage.INVALID_PASSWORD.getMessage())).when(accessValidationService).validateUserBeforeRegistration(registrationRequest);

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> authService.registration(registrationRequest)
        );

        assertEquals(
                ApiErrorMessage.INVALID_PASSWORD.getMessage(),
                exception.getMessage()
        );

        verify(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        verify(userMapper, never()).convertUserRequestToUser(registrationRequest);
        verify(roleRepository, never()).findByName(userRole.getName());
        verify(userRepository, never()).save(testUser);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void registration_UserWithThisUsernameExist_DataExistsException() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "testusername",
                "testemail",
                "password",
                "password"
        );

        doThrow(new DataExistsException(ApiErrorMessage.USER_WITH_THIS_USERNAME_EXIST.getMessage(registrationRequest.getUsername()))).when(accessValidationService).validateUserBeforeRegistration(registrationRequest);

        DataExistsException exception = assertThrows(
                DataExistsException.class,
                () -> authService.registration(registrationRequest)
        );

        assertEquals(
                ApiErrorMessage.USER_WITH_THIS_USERNAME_EXIST.getMessage(registrationRequest.getUsername()),
                exception.getMessage()
        );

        verify(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        verify(userMapper, never()).convertUserRequestToUser(registrationRequest);
        verify(roleRepository, never()).findByName(userRole.getName());
        verify(userRepository, never()).save(testUser);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void registration_UserWithThisEmailExist_DataExistsException() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "testusername",
                "testemail",
                "password",
                "password"
        );

        doThrow(new DataExistsException(ApiErrorMessage.USER_WITH_THIS_EMAIL_EXIST.getMessage(registrationRequest.getEmail()))).when(accessValidationService).validateUserBeforeRegistration(registrationRequest);

        DataExistsException exception = assertThrows(
                DataExistsException.class,
                () -> authService.registration(registrationRequest)
        );

        assertEquals(
                ApiErrorMessage.USER_WITH_THIS_EMAIL_EXIST.getMessage(registrationRequest.getEmail()),
                exception.getMessage()
        );

        verify(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        verify(userMapper, never()).convertUserRequestToUser(registrationRequest);
        verify(roleRepository, never()).findByName(userRole.getName());
        verify(userRepository, never()).save(testUser);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void registration_RoleNotFoundByName_NotFoundException() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "testusername",
                "testemail",
                "password",
                "password"
        );

        doNothing().when(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        when(userMapper.convertUserRequestToUser(registrationRequest))
                .thenReturn(testUser);
        when(roleRepository.findByName(userRole.getName()))
                .thenThrow(new NotFoundException(ApiErrorMessage.ROLE_NOT_FOUND_BY_NAME.getMessage(userRole.getName())));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> authService.registration(registrationRequest)
        );

        assertEquals(
                ApiErrorMessage.ROLE_NOT_FOUND_BY_NAME.getMessage(userRole.getName()),
                exception.getMessage()
        );

        verify(accessValidationService).validateUserBeforeRegistration(registrationRequest);
        verify(userMapper).convertUserRequestToUser(registrationRequest);
        verify(roleRepository).findByName(userRole.getName());
        verify(userRepository, never()).save(testUser);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(testUser);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void refreshToken_Success_CustomResponseWithUserProfileDto() {
        String refreshTokenValue = testRefreshToken.getToken();

        when(refreshTokenService.validateAndRefreshToken(refreshTokenValue))
                .thenReturn(testRefreshToken);
        when(jwtTokenProvider.generateToken(testUser))
                .thenReturn(testUserProfileDto.getToken());
        when(userMapper.createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken()))
                .thenReturn(testUserProfileDto);

        CustomResponse<UserProfileDto> response = authService.refreshToken(refreshTokenValue);

        assertNotNull(response);
        assertEquals(testUserProfileDto, response.getBody());

        verify(refreshTokenService).validateAndRefreshToken(refreshTokenValue);
        verify(jwtTokenProvider).generateToken(testUser);
        verify(userMapper).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }

    @Test
    void refreshToken_RefreshTokenNotFoundByToken_thrownNotFoundException() {
        String refreshTokenValue = testRefreshToken.getToken();

        when(refreshTokenService.validateAndRefreshToken(refreshTokenValue))
                .thenThrow(new NotFoundException(ApiErrorMessage.REFRESH_TOKEN_NOT_FOUND_BY_TOKEN.getMessage(refreshTokenValue)));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> authService.refreshToken(refreshTokenValue)
        );

        assertEquals(
                ApiErrorMessage.REFRESH_TOKEN_NOT_FOUND_BY_TOKEN.getMessage(refreshTokenValue),
                exception.getMessage()
        );

        verify(refreshTokenService).validateAndRefreshToken(refreshTokenValue);
        verify(jwtTokenProvider, never()).generateToken(testUser);
        verify(userMapper, never()).createUserProfileDto(testUser, testUserProfileDto.getToken(), testRefreshToken.getToken());
    }
}
