package practical.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practical.post.model.constants.ApiLogMessage;
import practical.post.model.dto.user.UserDto;
import practical.post.model.dto.user.UserSearchDto;
import practical.post.model.request.user.UpdateUserRequest;
import practical.post.model.request.user.UserRequest;
import practical.post.model.request.user.UserSearchRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.service.UserService;
import practical.post.utils.ApiUtils;


@RequestMapping("${end.point.users}")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("${end.point.id}")
    public ResponseEntity<CustomResponse<UserDto>> findUserById(@PathVariable int id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<UserDto> userDtoCustomResponse = userService.findById(id);

        return ResponseEntity.ok(userDtoCustomResponse);
    }

    @PostMapping("${end.point.create}")
    public ResponseEntity<CustomResponse<UserDto>> saveUser(@Valid @RequestBody UserRequest userRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<UserDto> userDtoCustomResponse = userService.save(userRequest);

        return ResponseEntity.ok(userDtoCustomResponse);
    }

    @PutMapping("${end.point.update}")
    public ResponseEntity<CustomResponse<UserDto>> updateUser(@PathVariable int id,
                                                              @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<UserDto> userDtoCustomResponse = userService.update(id, updateUserRequest);

        return ResponseEntity.ok(userDtoCustomResponse);
    }

    @DeleteMapping("${end.point.delete}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        userService.delete(id);
//        return ResponseEntity.ok().build();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("${end.point.all}")
    public ResponseEntity<CustomResponse<PaginationResponse<UserSearchDto>>> findAllUsers(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<UserSearchDto>> response = userService.findAllByPage(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.point.search}")
    public ResponseEntity<CustomResponse<PaginationResponse<UserSearchDto>>> findAllUsersWithCriteria(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit,
            @RequestBody UserSearchRequest userSearchRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<UserSearchDto>> response = userService.findAllByPageWithCriteria(userSearchRequest, pageable);

        return ResponseEntity.ok(response);
    }
}
