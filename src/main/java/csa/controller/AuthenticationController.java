package csa.controller;

import csa.dto.user.UserLoginRequestDto;
import csa.dto.user.UserLoginResponseDto;
import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.exception.RegistrationException;
import csa.service.security.AuthenticationService;
import csa.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }

    @PostMapping("/login")
    private UserLoginResponseDto loginResponseDto(
            @RequestBody @Valid UserLoginRequestDto userRequestDto) {
        return authenticationService.authenticate(userRequestDto);
    }
}
