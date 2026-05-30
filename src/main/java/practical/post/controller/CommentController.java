package practical.post.controller;

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
import practical.post.model.dto.comment.CommentDto;
import practical.post.model.dto.comment.CommentSearchDto;
import practical.post.model.request.comment.CommentRequest;
import practical.post.model.request.comment.CommentSearchRequest;
import practical.post.model.request.comment.UpdateCommentRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.security.CustomUserDetails;
import practical.post.service.CommentService;
import practical.post.utils.ApiUtils;

@RequestMapping("${end.point.comments}")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @GetMapping("${end.point.id}")
    public ResponseEntity<CustomResponse<CommentDto>> findPostById(@PathVariable int id) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<CommentDto> postDtoCustomResponse = commentService.findById(id);

        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @PostMapping("${end.point.create}")
    public ResponseEntity<CustomResponse<CommentDto>> savePost(@Valid @RequestBody CommentRequest commentRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<CommentDto> postDtoCustomResponse = commentService.save(commentRequest, customUserDetails.getUser().getId());

        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @PutMapping("${end.point.update}")
    public ResponseEntity<CustomResponse<CommentDto>> updatePost(@PathVariable int id,
                                                                 @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
                                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        CustomResponse<CommentDto> postDtoCustomResponse = commentService.update(id, customUserDetails.getUser().getId(), updateCommentRequest);

        return ResponseEntity.ok(postDtoCustomResponse);
    }

    @DeleteMapping("${end.point.delete}")
    public ResponseEntity<Void> deletePost(@PathVariable int id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        commentService.delete(id, customUserDetails.getUser().getId());
//        return ResponseEntity.ok().build();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("${end.point.all}")
    public ResponseEntity<CustomResponse<PaginationResponse<CommentSearchDto>>> findAllPosts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<CommentSearchDto>> response = commentService.findAllByPage(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.point.search}")
    public ResponseEntity<CustomResponse<PaginationResponse<CommentSearchDto>>> findAllPostsWithCriteria(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit,
            @RequestBody CommentSearchRequest commentSearchRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<CommentSearchDto>> response = commentService.findAllByPageWithCriteria(commentSearchRequest, pageable);

        return ResponseEntity.ok(response);
    }
}
