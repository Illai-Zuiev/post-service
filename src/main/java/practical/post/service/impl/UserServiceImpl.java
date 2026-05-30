package practical.post.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import practical.post.mapper.UserMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.user.UserDto;
import practical.post.model.dto.user.UserSearchDto;
import practical.post.model.entity.User;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.user.UpdateUserRequest;
import practical.post.model.request.user.UserRequest;
import practical.post.model.request.user.UserSearchRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.repository.UserRepository;
import practical.post.repository.criteria.UserSearchCriteria;
import practical.post.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public CustomResponse<UserDto> findById(int id) {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id))
        );

        UserDto userDto = userMapper.convertUserToUserDto(user);

        return CustomResponse.createSuccessful(userDto);
    }

    @Override
    public CustomResponse<UserDto> save(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_USERNAME_EXIST.getMessage(userRequest.getUsername()));
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_EMAIL_EXIST.getMessage(userRequest.getEmail()));
        }

        User user = userMapper.convertUserRequestToUser(userRequest);

        user = userRepository.save(user);

        UserDto userDto = userMapper.convertUserToUserDto(user);

        return CustomResponse.createSuccessful(userDto);
    }

    @Override
    @Transactional
    public CustomResponse<UserDto> update(int id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id))
        );

        if (!user.getEmail().equals(updateUserRequest.getEmail()) && userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_EMAIL_EXIST.getMessage(updateUserRequest.getEmail()));
        }

        if (!user.getUsername().equals(updateUserRequest.getUsername()) && userRepository.existsByUsername(updateUserRequest.getUsername())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_USERNAME_EXIST.getMessage(updateUserRequest.getUsername()));
        }

        user.setEmail(updateUserRequest.getEmail());
        user.setUsername(updateUserRequest.getUsername());
        user.setUpdated(LocalDateTime.now());

        UserDto userDto = userMapper.convertUserToUserDto(user);

        return CustomResponse.createSuccessful(userDto);
    }

    @Override
    @Transactional
    public void delete(int id) {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id))
        );

        user.setUpdated(LocalDateTime.now());
        user.setDeleted(true);
    }

    @Override
    public CustomResponse<PaginationResponse<UserSearchDto>> findAllByPage(Pageable pageable) {
        Page<UserSearchDto> users = userRepository.findAll(pageable)
                .map(userMapper::convertUserToUserSearchDto);

        PaginationResponse<UserSearchDto> response = new PaginationResponse<>(
                users.getContent(),
                new PaginationResponse.Pagination(
                        users.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        users.getTotalPages()
                )
        );

        return CustomResponse.createSuccessful(response);
    }

    @Override
    public CustomResponse<PaginationResponse<UserSearchDto>> findAllByPageWithCriteria(UserSearchRequest userSearchRequest, Pageable pageable) {
        Specification<User> specification = new UserSearchCriteria(userSearchRequest);

        Page<UserSearchDto> users = userRepository.findAll(specification, pageable)
                .map(userMapper::convertUserToUserSearchDto);

        PaginationResponse<UserSearchDto> response = new PaginationResponse<>(
                users.getContent(),
                new PaginationResponse.Pagination(
                        users.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        users.getTotalPages()
                )
        );

        return CustomResponse.createSuccessful(response);
    }
}
