package csa.service;

import static csa.util.RoleTestUtil.createAdminRole;
import static csa.util.RoleTestUtil.createCustomerRole;
import static csa.util.UserTestUtil.createCustomerUser;
import static csa.util.UserTestUtil.createUserRegRequestDto;
import static csa.util.UserTestUtil.createUserResponseDto;
import static csa.util.UserTestUtil.createUserResponseWithRolesDto;
import static csa.util.UserTestUtil.createUserUpdateProfileRequestDto;
import static csa.util.UserTestUtil.createUserUpdateRolesRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.dto.user.UserResponseWithRolesDto;
import csa.dto.user.UserUpdateProfileRequestDto;
import csa.dto.user.UserUpdateRolesRequestDto;
import csa.exception.RegistrationException;
import csa.mapper.UserMapper;
import csa.model.Role;
import csa.model.User;
import csa.model.enums.RoleName;
import csa.repository.RoleRepository;
import csa.repository.UserRepository;
import csa.service.user.UserServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("register should return correct UserResponseDto")
    void register_CorrectData_True() throws RegistrationException {
        UserRegRequestDto requestDto = createUserRegRequestDto();
        User user = createCustomerUser();
        Role role = createCustomerRole();
        UserResponseDto expected = createUserResponseDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(roleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        UserResponseDto result = userService.register(requestDto);

        assertEquals(expected, result);
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userMapper).toModel(requestDto);
        verify(roleRepository).findByName(RoleName.ROLE_CUSTOMER);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("getUserProfile should return correct UserResponseWithRolesDto")
    void getUserProfile_UserWithIdThree_True() {
        Long userId = 3L;
        User user = createCustomerUser();
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();

        when(userRepository.findWithRolesById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDtoWithRoles(user)).thenReturn(expected);
        UserResponseWithRolesDto result = userService.getUserProfile(userId);

        assertEquals(expected, result);
        verify(userRepository).findWithRolesById(userId);
        verify(userMapper).toDtoWithRoles(user);
    }

    @Test
    @DisplayName("updateUserProfile should return correct UserResponseWithRolesDto")
    void updateUserProfile_ChangeFirstNameToJohn_True() {
        Long userId = 3L;
        User user = createCustomerUser();
        UserUpdateProfileRequestDto requestDto = createUserUpdateProfileRequestDto();
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();
        expected.setFirstName("John");

        when(userRepository.findWithRolesById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserProfile(user, requestDto);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoWithRoles(user)).thenReturn(expected);
        UserResponseWithRolesDto result = userService.updateUserProfile(userId, requestDto);

        assertEquals(expected, result);
        verify(userRepository).findWithRolesById(userId);
        verify(userMapper).updateUserProfile(user, requestDto);
        verify(userRepository).save(user);
        verify(userMapper).toDtoWithRoles(user);
    }

    @Test
    @DisplayName("updateUserRoles should return UserResponseWithRolesDto with correct roles")
    void updateUserRoles_AddAdminRole_True() {
        Long userId = 3L;
        User user = createCustomerUser();
        UserUpdateRolesRequestDto requestDto = createUserUpdateRolesRequestDto();
        requestDto.getRoles().add(RoleName.ROLE_ADMIN);
        List<Role> roles = List.of(createCustomerRole(), createAdminRole());
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();
        expected.getRoles().add(RoleName.ROLE_ADMIN.name());

        when(userRepository.findWithRolesById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(requestDto.getRoles())).thenReturn(roles);
        when(userMapper.toDtoWithRoles(user)).thenReturn(expected);
        UserResponseWithRolesDto result = userService.updateUserRoles(userId, requestDto);

        assertEquals(expected, result);
        verify(userRepository).findWithRolesById(userId);
        verify(roleRepository).findByNameIn(requestDto.getRoles());
        verify(userMapper).toDtoWithRoles(user);
    }

}