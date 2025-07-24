package csa.service.user;

import csa.dto.UserRegRequestDto;
import csa.dto.UserResponseDto;
import csa.dto.UserResponseWithRolesDto;
import csa.dto.UserUpdateProfileRequestDto;
import csa.dto.UserUpdateRolesRequestDto;
import csa.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegRequestDto userRegRequest)
            throws RegistrationException;

    UserResponseWithRolesDto getUserProfile(Long userId);

    UserResponseWithRolesDto updateUserProfile(
            Long userId, UserUpdateProfileRequestDto requestDto);

    UserResponseWithRolesDto updateUserRoles(
            Long userId, UserUpdateRolesRequestDto requestDto);
}
