package practical.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import practical.post.model.dto.comment.CommentDto;
import practical.post.model.dto.comment.CommentSearchDto;
import practical.post.model.entity.Comment;
import practical.post.model.request.comment.CommentRequest;

import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {Objects.class}
)
public interface CommentMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "owner.id", source = "user.id")
    @Mapping(target = "owner.email", source = "user.email")
    @Mapping(target = "owner.username", source = "user.username")
    CommentDto convertCommentToCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Comment convertCommentRequestToComment(CommentRequest commentRequest);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "owner.id", source = "user.id")
    @Mapping(target = "owner.email", source = "user.email")
    @Mapping(target = "owner.username", source = "user.username")
    CommentSearchDto convertCommentToCommentSearchDto(Comment comment);
}
