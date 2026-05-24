package practical.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import practical.post.model.dto.role.RoleDto;
import practical.post.model.dto.user.UserDto;
import practical.post.model.dto.user.UserProfileDto;
import practical.post.model.dto.user.UserSearchDto;
import practical.post.model.entity.Role;
import practical.post.model.entity.User;
import practical.post.model.enums.RegistrationStatus;
import practical.post.model.request.auth.RegistrationRequest;
import practical.post.model.request.user.UserRequest;

import java.util.Collection;
import java.util.List;
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
    @Mapping(target = "roles", ignore = true)
    User convertUserRequestToUser(UserRequest userRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User convertUserRequestToUser(RegistrationRequest registrationRequest);

    UserSearchDto convertUserToUserSearchDto(User user);

    @Mapping(target = "roles", expression = "java(convertListRoleToListRoleDto(user.getRoles()))")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    UserProfileDto createUserProfileDto(User user, String token, String refreshToken);

    default List<RoleDto> convertListRoleToListRoleDto(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .toList();
    }
}
