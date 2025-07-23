package csa.service.user;

import csa.dto.UserRegRequestDto;
import csa.dto.UserResponseDto;
import csa.exception.RegistrationException;
import csa.mapper.UserMapper;
import csa.model.Role;
import csa.model.User;
import csa.model.enums.RoleName;
import csa.repository.RoleRepository;
import csa.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
