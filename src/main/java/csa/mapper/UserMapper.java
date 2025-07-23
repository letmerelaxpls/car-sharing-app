package csa.mapper;

import csa.config.MapperConfig;
import csa.dto.UserRegRequestDto;
import csa.dto.UserResponseDto;
import csa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toModel(UserRegRequestDto userRequest);

    UserResponseDto toDto(User user);
}
