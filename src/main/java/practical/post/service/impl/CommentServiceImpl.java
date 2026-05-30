package practical.post.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import practical.post.mapper.CommentMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.comment.CommentDto;
import practical.post.model.dto.comment.CommentSearchDto;
import practical.post.model.entity.Comment;
import practical.post.model.entity.Post;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.comment.CommentRequest;
import practical.post.model.request.comment.CommentSearchRequest;
import practical.post.model.request.comment.UpdateCommentRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.repository.CommentRepository;
import practical.post.repository.PostRepository;
import practical.post.repository.UserRepository;
import practical.post.repository.criteria.CommentSearchCriteria;
import practical.post.service.CommentService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AccessValidationService accessValidationService;
    private final CommentMapper commentMapper;

    @Override
    public CustomResponse<CommentDto> findById(int id) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(id))
        );

        CommentDto commentDto = commentMapper.convertCommentToCommentDto(comment);

        return CustomResponse.createSuccessful(commentDto);
    }

    @Override
    public CustomResponse<CommentDto> save(CommentRequest commentRequest, int userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId))
        );

        Post post = postRepository.findByIdAndDeletedFalse(commentRequest.getPostId()).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(userId))
        );

        Comment comment = commentMapper.convertCommentRequestToComment(commentRequest);
        comment.setUser(user);
        comment.setPost(post);

        comment = commentRepository.save(comment);

        CommentDto commentDto = commentMapper.convertCommentToCommentDto(comment);

        return CustomResponse.createSuccessful(commentDto);
    }

    @Override
    @Transactional
    public CustomResponse<CommentDto> update(int id, int currentUserId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(id))
        );

        accessValidationService.validateOwner(currentUserId, comment.getUser().getId());

        comment.setContent(updateCommentRequest.getContent());
        comment.setUpdated(LocalDateTime.now());

        CommentDto commentDto = commentMapper.convertCommentToCommentDto(comment);

        return CustomResponse.createSuccessful(commentDto);
    }

    @Override
    @Transactional
    public void delete(int id, int currentUserId) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(id))
        );

        accessValidationService.validateOwner(currentUserId, comment.getUser().getId());

        comment.setUpdated(LocalDateTime.now());
        comment.setDeleted(true);
    }

    @Override
    public CustomResponse<PaginationResponse<CommentSearchDto>> findAllByPage(Pageable pageable) {
        Page<CommentSearchDto> comments = commentRepository.findAll(pageable)
                .map(commentMapper::convertCommentToCommentSearchDto);

        PaginationResponse<CommentSearchDto> response = new PaginationResponse<>(
                comments.getContent(),
                new PaginationResponse.Pagination(
                        comments.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        comments.getTotalPages()
                )
        );

        return CustomResponse.createSuccessful(response);
    }

    @Override
    public CustomResponse<PaginationResponse<CommentSearchDto>> findAllByPageWithCriteria(CommentSearchRequest commentSearchRequest, Pageable pageable) {
        Specification<Comment> specification = new CommentSearchCriteria(commentSearchRequest);

        Page<CommentSearchDto> comments = commentRepository.findAll(specification, pageable)
                .map(commentMapper::convertCommentToCommentSearchDto);

        PaginationResponse<CommentSearchDto> response = new PaginationResponse<>(
                comments.getContent(),
                new PaginationResponse.Pagination(
                        comments.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        comments.getTotalPages()
                )
        );

        return CustomResponse.createSuccessful(response);
    }
}
