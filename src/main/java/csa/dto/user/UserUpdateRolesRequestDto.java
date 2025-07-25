package csa.dto.user;

import csa.model.enums.RoleName;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class UserUpdateRolesRequestDto {
    private Set<RoleName> roles = new HashSet<>();
}
