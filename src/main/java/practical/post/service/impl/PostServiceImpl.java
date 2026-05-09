package practical.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.post.PostDto;
import practical.post.model.entity.Post;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.response.CustomResponse;
import practical.post.repository.PostRepository;
import practical.post.service.PostService;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public CustomResponse<PostDto> findById(int id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id))
        );

        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setCreated(post.getCreated());

        return CustomResponse.createSuccessful(postDto);
    }
}
