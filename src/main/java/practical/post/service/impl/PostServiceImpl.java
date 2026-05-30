package practical.post.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import practical.post.mapper.PostMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.post.PostDto;
import practical.post.model.dto.post.PostSearchDto;
import practical.post.model.entity.Post;
import practical.post.model.entity.User;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.PostSearchRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.repository.PostRepository;
import practical.post.repository.UserRepository;
import practical.post.repository.criteria.PostSearchCriteria;
import practical.post.service.PostService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AccessValidationService accessValidationService;
    private final PostMapper postMapper;

    @Override
    public CustomResponse<PostDto> findById(int id) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id))
        );

        PostDto postDto = postMapper.convertPostToPostDto(post);

        return CustomResponse.createSuccessful(postDto);
    }

    @Override
    public CustomResponse<PostDto> save(PostRequest postRequest, int userId) {
        accessValidationService.validatePostBeforeCreating(postRequest);

        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId))
        );

        Post post = postMapper.convertPostRequestToPost(postRequest);
        post.setUser(user);

        post = postRepository.save(post);

        PostDto postDto = postMapper.convertPostToPostDto(post);

        return CustomResponse.createSuccessful(postDto);
    }

    @Override
    @Transactional
    public CustomResponse<PostDto> update(int id, int currentUserId, UpdatePostRequest updatePostRequest) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id))
        );

        accessValidationService.validateOwner(currentUserId, post.getUser().getId());

        if (!post.getTitle().equals(updatePostRequest.getTitle()) && postRepository.existsByTitle(updatePostRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_WITH_THIS_TITLE_EXIST.getMessage(updatePostRequest.getTitle()));
        }

        post.setTitle(updatePostRequest.getTitle());
        post.setContent(post.getContent());
        post.setUpdated(LocalDateTime.now());

        PostDto postDto = postMapper.convertPostToPostDto(post);

        return CustomResponse.createSuccessful(postDto);
    }

    @Override
    @Transactional
    public void delete(int id, int currentUserId) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id))
        );

        accessValidationService.validateOwner(currentUserId, post.getUser().getId());

        post.setUpdated(LocalDateTime.now());
        post.setDeleted(true);
    }

    @Override
    public CustomResponse<PaginationResponse<PostSearchDto>> findAllByPage(Pageable pageable) {
        Page<PostSearchDto> posts = postRepository.findAll(pageable)
                .map(postMapper::convertPostToPostSearchDto);

        PaginationResponse<PostSearchDto> response = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        posts.getTotalPages()
                )
        );

        return CustomResponse.createSuccessful(response);
    }

    @Override
    public CustomResponse<PaginationResponse<PostSearchDto>> findAllByPageWithCriteria(PostSearchRequest postSearchRequest, Pageable pageable) {
        Specification<Post> specification = new PostSearchCriteria(postSearchRequest);

        Page<PostSearchDto> posts = postRepository.findAll(specification, pageable)
                .map(postMapper::convertPostToPostSearchDto);

        PaginationResponse<PostSearchDto> response = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        posts.getTotalPages()
                )
        );

        return CustomResponse.createSuccessful(response);
    }
}
