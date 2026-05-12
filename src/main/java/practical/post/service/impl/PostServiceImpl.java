package practical.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practical.post.mapper.PostMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.post.PostDto;
import practical.post.model.entity.Post;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.repository.PostRepository;
import practical.post.service.PostService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public CustomResponse<PostDto> findById(int id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id))
        );

        PostDto postDto = postMapper.convertPostToPostDto(post);

        return CustomResponse.createSuccessful(postDto);
    }

    @Override
    public CustomResponse<PostDto> save(PostRequest postRequest) {
        if (postRepository.existsByTitle(postRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_WITH_THIS_TITLE_EXIST.getMessage(postRequest.getTitle()));
        }

        Post post = postMapper.convertPostRequestToPost(postRequest);

        post = postRepository.save(post);

        PostDto postDto = postMapper.convertPostToPostDto(post);

        return CustomResponse.createSuccessful(postDto);
    }

    @Override
    public CustomResponse<PostDto> update(int id, UpdatePostRequest postRequest) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id))
        );

        if (!post.getTitle().equals(postRequest.getTitle()) && postRepository.existsByTitle(postRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_WITH_THIS_TITLE_EXIST.getMessage(postRequest.getTitle()));
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(post.getContent());
        post.setUpdated(LocalDateTime.now());

        post = postRepository.save(post);

        PostDto postDto = postMapper.convertPostToPostDto(post);

        return CustomResponse.createSuccessful(postDto);
    }
}
