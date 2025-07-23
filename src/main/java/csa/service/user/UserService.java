package csa.service.user;

import csa.dto.UserRegRequestDto;
import csa.dto.UserResponseDto;
import csa.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegRequestDto userRegRequest)
            throws RegistrationException;
}
