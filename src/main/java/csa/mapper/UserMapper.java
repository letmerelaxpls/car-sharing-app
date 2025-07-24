package csa.mapper;

import csa.config.MapperConfig;
import csa.dto.UserRegRequestDto;
import csa.dto.UserResponseDto;
import csa.dto.UserResponseWithRolesDto;
import csa.dto.UserUpdateProfileRequestDto;
import csa.model.User;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toModel(UserRegRequestDto userRequest);

    UserResponseDto toDto(User user);

    @Mapping(target = "roles", ignore = true)
    UserResponseWithRolesDto toDtoWithRoles(User user);

    @AfterMapping
    default void setRoles(@MappingTarget UserResponseWithRolesDto userDto, User user) {
        userDto.setRoles(user
                .getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet()));
    }

    void updateUserProfile(@MappingTarget User user,
                           UserUpdateProfileRequestDto userRequest);
}
