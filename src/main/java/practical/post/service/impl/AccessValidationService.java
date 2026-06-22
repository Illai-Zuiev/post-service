package practical.post.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.enums.UserSystemRole;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.InvalidDataException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.auth.RegistrationRequest;
import practical.post.model.request.post.PostRequest;
import practical.post.repository.PostRepository;
import practical.post.repository.UserRepository;
import practical.post.security.utils.PasswordUtils;

@Service
@RequiredArgsConstructor
public class AccessValidationService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void validateUserBeforeRegistration(RegistrationRequest registrationRequest) {
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            throw new InvalidDataException(ApiErrorMessage.MISMATCHED_PASSWORDS.getMessage());
        }

        if (PasswordUtils.isNotValidPassword(registrationRequest.getPassword())) {
            throw new InvalidDataException(ApiErrorMessage.INVALID_PASSWORD.getMessage());
        }

        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_USERNAME_EXIST.getMessage(registrationRequest.getUsername()));
        }

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_EMAIL_EXIST.getMessage(registrationRequest.getEmail()));
        }
    }

    public void validatePostBeforeCreating(PostRequest postRequest) {
        if (postRepository.existsByTitle(postRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_WITH_THIS_TITLE_EXIST.getMessage(postRequest.getTitle()));
        }
    }

    public boolean isUserAdminOrSuperAdmin(int userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(
                        () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId))
                )
                .getRoles()
                .stream()
                .anyMatch(role -> role.getUserSystemRole() == UserSystemRole.ADMIN || role.getUserSystemRole() == UserSystemRole.SUPER_ADMIN);
    }

    @SneakyThrows
    public void validateOwner(int currentUserId, int ownerUserId) {
        if (currentUserId != ownerUserId && !isUserAdminOrSuperAdmin(currentUserId)) {
            throw new AccessDeniedException(ApiErrorMessage.ACCESS_DENIED.getMessage());
        }
    }
}
