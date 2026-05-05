package practical.post.service;

import practical.post.model.dto.post.PostDto;
import practical.post.model.response.CustomResponse;

public interface PostService {
    CustomResponse<PostDto> getById(int id);
}