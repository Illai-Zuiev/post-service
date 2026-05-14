package practical.post.service;

import org.springframework.data.domain.Pageable;
import practical.post.model.dto.post.PostDto;
import practical.post.model.dto.post.PostSearchDto;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;

public interface PostService {
    CustomResponse<PostDto> findById(int id);

    CustomResponse<PostDto> save(PostRequest postRequest);

    CustomResponse<PostDto> update(int id, UpdatePostRequest postRequest);

    void delete(int id);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllByPage(Pageable pageable);
}