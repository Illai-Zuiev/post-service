package practical.post.service;

import practical.post.model.dto.post.PostDto;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;

public interface PostService {
    CustomResponse<PostDto> findById(int id);
    CustomResponse<PostDto> save(PostRequest postRequest);
    CustomResponse<PostDto> update(int id, UpdatePostRequest postRequest);
}