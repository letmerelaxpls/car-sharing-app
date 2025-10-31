package csa.controller;

import csa.dto.user.UserLoginRequestDto;
import csa.dto.user.UserLoginResponseDto;
import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.exception.RegistrationException;
import csa.service.security.AuthenticationService;
import csa.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "Endpoints for user registration and authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration",
            description = "Endpoint for user registration")
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }

    @Operation(summary = "User authentication",
            description = "Endpoint for user authentication")
    @PostMapping("/login")
    private UserLoginResponseDto loginResponseDto(
            @RequestBody @Valid UserLoginRequestDto userRequestDto) {
        return authenticationService.authenticate(userRequestDto);
    }
}
