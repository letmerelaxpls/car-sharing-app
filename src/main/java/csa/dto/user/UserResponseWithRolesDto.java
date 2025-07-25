package csa.dto.user;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class UserResponseWithRolesDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles = new HashSet<>();
}
