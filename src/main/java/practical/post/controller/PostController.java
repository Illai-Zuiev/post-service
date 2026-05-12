package practical.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practical.post.model.constants.ApiLogMessage;
import practical.post.model.dto.post.PostDto;
import practical.post.model.request.post.PostRequest;
import practical.post.model.response.CustomResponse;
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
}
