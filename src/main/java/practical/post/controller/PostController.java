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
import practical.post.model.dto.post.PostDto;
import practical.post.model.dto.post.PostSearchDto;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.PostSearchRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.service.PostService;
import practical.post.utils.ApiUtils;

@RequiredArgsConstructor
@RestController
@RequestMapping("${end.point.posts}")
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping("${end.point.id}")
    public ResponseEntity<CustomResponse<PostDto>> findPostById(@PathVariable int id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        CustomResponse<PostDto> postDtoCustomResponse = postService.findById(id);
        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @PostMapping("${end.point.create}")
    public ResponseEntity<CustomResponse<PostDto>> savePost(@Valid @RequestBody PostRequest postRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        CustomResponse<PostDto> postDtoCustomResponse = postService.save(postRequest);
        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @PutMapping("${end.point.update}")
    public ResponseEntity<CustomResponse<PostDto>> updatePost(@PathVariable int id,
                                                              @Valid @RequestBody UpdatePostRequest updatePostRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        CustomResponse<PostDto> postDtoCustomResponse = postService.update(id, updatePostRequest);
        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @DeleteMapping("${end.point.delete}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        postService.delete(id);
//        return ResponseEntity.ok().build();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("${end.point.all}")
    public ResponseEntity<CustomResponse<PaginationResponse<PostSearchDto>>> findAllPosts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);

        CustomResponse<PaginationResponse<PostSearchDto>> response = postService.findAllByPage(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.point.search}")
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
