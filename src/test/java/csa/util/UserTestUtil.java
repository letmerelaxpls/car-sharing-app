package csa.util;

import static csa.util.RoleTestUtil.createAdminRole;
import static csa.util.RoleTestUtil.createCustomerRole;

import csa.dto.user.UserLoginRequestDto;
import csa.dto.user.UserLoginResponseDto;
import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.dto.user.UserResponseWithRolesDto;
import csa.dto.user.UserUpdateProfileRequestDto;
import csa.dto.user.UserUpdateRolesRequestDto;
import csa.model.Role;
import csa.model.User;
import csa.model.enums.RoleName;
import java.util.HashSet;
import java.util.Set;

public class UserTestUtil {
    private static final String USER_EMAIL = "test@gmail.com";
    private static final String USER_NAME = "test";
    private static final String USER_PASSWORD = "123456";
    
    public static User createUser(Long userId, Role role) {
        User user = new User();
        user.setId(userId);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setRoles(Set.of(role));
        return user;
    }
    
    public static User createCustomerUser() {
        User user = new User();
        user.setId(3L);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_NAME);
        user.setLastName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setRoles(Set.of(createCustomerRole()));
        return user;
    }

    public static User createAdminUser() {
        User user = new User();
        user.setId(4L);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_NAME);
        user.setLastName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setRoles(Set.of(createAdminRole()));
        return user;
    }

    public static UserRegRequestDto createUserRegRequestDto() {
        UserRegRequestDto requestDto = new UserRegRequestDto();
        requestDto.setEmail(USER_EMAIL);
        requestDto.setFirstName(USER_NAME);
        requestDto.setLastName(USER_NAME);
        requestDto.setPassword(USER_PASSWORD);
        requestDto.setRepeatPassword(USER_PASSWORD);
        return requestDto;
    }

    public static UserResponseDto createUserResponseDto() {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(3L);
        responseDto.setEmail(USER_EMAIL);
        responseDto.setFirstName(USER_NAME);
        responseDto.setLastName(USER_NAME);
        return responseDto;
    }

    public static UserResponseWithRolesDto createUserResponseWithRolesDto() {
        UserResponseWithRolesDto responseWithRolesDto =
                new UserResponseWithRolesDto();
        responseWithRolesDto.setId(3L);
        responseWithRolesDto.setEmail(USER_EMAIL);
        responseWithRolesDto.setFirstName(USER_NAME);
        responseWithRolesDto.setLastName(USER_NAME);
        responseWithRolesDto.setRoles(
                new HashSet<>(Set.of(RoleName.ROLE_CUSTOMER.name())));
        return responseWithRolesDto;
    }

    public static UserUpdateProfileRequestDto createUserUpdateProfileRequestDto() {
        UserUpdateProfileRequestDto requestDto = new UserUpdateProfileRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName(USER_NAME);
        return requestDto;
    }

    public static UserUpdateRolesRequestDto createUserUpdateRolesRequestDto() {
        UserUpdateRolesRequestDto requestDto = new UserUpdateRolesRequestDto();
        requestDto.setRoles(new HashSet<>(Set.of(RoleName.ROLE_CUSTOMER)));
        return requestDto;
    }

    public static UserLoginRequestDto createUserLoginRequestDto() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto();
        requestDto.setEmail(USER_EMAIL);
        requestDto.setPassword(USER_PASSWORD);
        return requestDto;
    }

    public static UserLoginResponseDto createUserLoginResponseDto() {
        return new UserLoginResponseDto("correctToken1234");
    }
}
