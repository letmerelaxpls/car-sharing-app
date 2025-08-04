package csa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEmailRequestDto(@NotBlank @Email String email) {

}

