package practical.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import practical.post.model.dto.user.UserDto;
import practical.post.model.dto.user.UserSearchDto;
import practical.post.model.entity.User;
import practical.post.model.enums.RegistrationStatus;
import practical.post.model.request.user.UserRequest;

import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {Objects.class, RegistrationStatus.class}
)
public interface UserMapper {
    UserDto convertUserToUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User convertUserRequestToUser(UserRequest userRequest);
    UserSearchDto convertUserToUserSearchDto(User user);
}
