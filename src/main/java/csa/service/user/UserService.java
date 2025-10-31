package csa.service.user;

import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.dto.user.UserResponseWithRolesDto;
import csa.dto.user.UserUpdateProfileRequestDto;
import csa.dto.user.UserUpdateRolesRequestDto;
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
