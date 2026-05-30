package practical.post.service;

import org.springframework.data.domain.Pageable;
import practical.post.model.dto.comment.CommentDto;
import practical.post.model.dto.comment.CommentSearchDto;
import practical.post.model.request.comment.CommentRequest;
import practical.post.model.request.comment.CommentSearchRequest;
import practical.post.model.request.comment.UpdateCommentRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;

public interface CommentService {
    CustomResponse<CommentDto> findById(int id);

    CustomResponse<CommentDto> save(CommentRequest commentRequest, int userId);

    CustomResponse<CommentDto> update(int id, int currentUserId, UpdateCommentRequest updateCommentRequest);

    void delete(int id, int currentUserId);

    CustomResponse<PaginationResponse<CommentSearchDto>> findAllByPage(Pageable pageable);

    CustomResponse<PaginationResponse<CommentSearchDto>> findAllByPageWithCriteria(CommentSearchRequest commentSearchRequest, Pageable pageable);
}