package practical.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import practical.post.model.constants.ApiLogMessage;
import practical.post.model.dto.post.PostDto;
import practical.post.model.dto.post.PostSearchDto;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.PostSearchRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.security.CustomUserDetails;
import practical.post.service.PostService;
import practical.post.utils.ApiUtils;

@RequestMapping("${end.point.posts}")
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Post", description = "Post methods")
public class PostController {
    private final PostService postService;

    @GetMapping("${end.point.id}")
    @Operation(
            summary = "post find",
            description = "find post by id"
    )
    public ResponseEntity<CustomResponse<PostDto>> findPostById(@PathVariable int id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<PostDto> postDtoCustomResponse = postService.findById(id);

        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @PostMapping("${end.point.create}")
    @Operation(
            summary = "post save",
            description = "save and return post"
    )
    public ResponseEntity<CustomResponse<PostDto>> savePost(@Valid @RequestBody PostRequest postRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<PostDto> postDtoCustomResponse = postService.save(postRequest, customUserDetails.getUser().getId());

        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @PutMapping("${end.point.update}")
    @Operation(
            summary = "post update",
            description = "update and return post"
    )
    public ResponseEntity<CustomResponse<PostDto>> updatePost(@PathVariable int id,
                                                              @Valid @RequestBody UpdatePostRequest updatePostRequest,
                                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<PostDto> postDtoCustomResponse = postService.update(id, customUserDetails.getUser().getId(), updatePostRequest);

        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @DeleteMapping("${end.point.delete}")
    @Operation(
            summary = "post delete",
            description = "delete post"
    )
    public ResponseEntity<Void> deletePost(@PathVariable int id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        postService.delete(id, customUserDetails.getUser().getId());
//        return ResponseEntity.ok().build();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("${end.point.all}")
    @Operation(
            summary = "post find all",
            description = "find and return all posts"
    )
    public ResponseEntity<CustomResponse<PaginationResponse<PostSearchDto>>> findAllPosts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<PostSearchDto>> response = postService.findAllByPage(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.point.search}")
    @Operation(
            summary = "post find all with criteria",
            description = "find and return all posts with some criteria"
    )
    public ResponseEntity<CustomResponse<PaginationResponse<PostSearchDto>>> findAllPostsWithCriteria(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit,
            @RequestBody PostSearchRequest postSearchRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<PostSearchDto>> response = postService.findAllByPageWithCriteria(postSearchRequest, pageable);

        return ResponseEntity.ok(response);
    }
}
