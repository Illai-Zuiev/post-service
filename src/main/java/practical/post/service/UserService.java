package practical.post.service;

import org.springframework.data.domain.Pageable;
import practical.post.model.dto.user.UserDto;
import practical.post.model.dto.user.UserSearchDto;
import practical.post.model.request.user.UpdateUserRequest;
import practical.post.model.request.user.UserRequest;
import practical.post.model.request.user.UserSearchRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;

public interface UserService {
    CustomResponse<UserDto> findById(int id);

    CustomResponse<UserDto> save(UserRequest userRequest);

    CustomResponse<UserDto> update(int id, UpdateUserRequest updateUserRequest);

    void delete(int id);

    CustomResponse<PaginationResponse<UserSearchDto>> findAllByPage(Pageable pageable);

    CustomResponse<PaginationResponse<UserSearchDto>> findAllByPageWithCriteria(UserSearchRequest userSearchRequest, Pageable pageable);
}
