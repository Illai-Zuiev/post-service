package practical.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import practical.post.model.dto.post.PostDto;
import practical.post.model.entity.Post;
import practical.post.model.request.post.PostRequest;

import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {Objects.class}
)
public interface PostMapper {
    PostDto convertPostToPostDto(Post post);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Post convertPostRequestToPost(PostRequest postRequest);
}
