package practical.post.service;

import org.springframework.data.domain.Pageable;
import practical.post.model.dto.post.PostDto;
import practical.post.model.dto.post.PostSearchDto;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.PostSearchRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;

public interface PostService {
    CustomResponse<PostDto> findById(int id);

    CustomResponse<PostDto> save(PostRequest postRequest, int userId);

    CustomResponse<PostDto> update(int id, int currentUserId, UpdatePostRequest updatePostRequest);

    void delete(int id, int currentUserId);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllByPage(Pageable pageable);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllByPageWithCriteria(PostSearchRequest postSearchRequest, Pageable pageable);
}