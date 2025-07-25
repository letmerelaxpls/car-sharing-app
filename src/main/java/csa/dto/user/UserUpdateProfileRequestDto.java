package csa.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateProfileRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
