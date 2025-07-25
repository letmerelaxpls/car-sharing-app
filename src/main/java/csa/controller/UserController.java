package csa.controller;

import csa.dto.user.UserResponseWithRolesDto;
import csa.dto.user.UserUpdateProfileRequestDto;
import csa.dto.user.UserUpdateRolesRequestDto;
import csa.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public UserResponseWithRolesDto getUserProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userService.getUserProfile(userId);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public UserResponseWithRolesDto updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UserUpdateProfileRequestDto requestDto) {
        Long userId = (Long) authentication.getPrincipal();
        return userService.updateUserProfile(userId, requestDto);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseWithRolesDto updateUserRoles(
            @PathVariable("id") Long userId,
            @RequestBody UserUpdateRolesRequestDto requestDto) {
        return userService.updateUserRoles(userId, requestDto);
    }
}
