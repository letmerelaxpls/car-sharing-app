package csa.service.user;

import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.dto.user.UserResponseWithRolesDto;
import csa.dto.user.UserUpdateProfileRequestDto;
import csa.dto.user.UserUpdateRolesRequestDto;
import csa.exception.EntityNotFoundException;
import csa.exception.RegistrationException;
import csa.mapper.UserMapper;
import csa.model.Role;
import csa.model.User;
import csa.model.enums.RoleName;
import csa.repository.RoleRepository;
import csa.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegRequestDto userRegRequest)
            throws RegistrationException {
        if (userRepository.existsByEmail(userRegRequest.getEmail())) {
            throw new RegistrationException("User with email "
                    + userRegRequest.getEmail() + " already exists");
        }
        String password = passwordEncoder.encode(userRegRequest.getPassword());
        User user = userMapper.toModel(userRegRequest);
        user.setPassword(password);
        Role role = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new RegistrationException("Role "
                        + RoleName.ROLE_CUSTOMER + " not found"));
        user.setRoles(Set.of(role));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseWithRolesDto getUserProfile(Long userId) {
        User user = findUserWithRoles(userId);
        return userMapper.toDtoWithRoles(user);
    }

    @Override
    public UserResponseWithRolesDto updateUserProfile(
            Long userId, UserUpdateProfileRequestDto requestDto) {
        User user = findUserWithRoles(userId);
        userMapper.updateUserProfile(user, requestDto);
        return userMapper.toDtoWithRoles(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseWithRolesDto updateUserRoles(
            Long userId, UserUpdateRolesRequestDto requestDto) {
        User user = findUserWithRoles(userId);
        Set<Role> roles = new HashSet<>(
                roleRepository.findByNameIn(requestDto.getRoles()));
        if (roles.size() != requestDto.getRoles().size()) {
            throw new EntityNotFoundException("Could not find 1 or more Roles with names: "
                    + requestDto.getRoles());
        }
        user.setRoles(roles);
        return userMapper.toDtoWithRoles(user);
    }

    private User findUserWithRoles(Long userId) {
        return userRepository.findWithRolesById(userId).orElseThrow(()
                -> new EntityNotFoundException("Could not find User with id: "
                + userId));
    }
}
