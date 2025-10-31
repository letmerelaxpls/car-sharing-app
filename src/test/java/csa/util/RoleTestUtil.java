package csa.util;

import csa.model.Role;
import csa.model.enums.RoleName;

public class RoleTestUtil {
    public static Role createCustomerRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(RoleName.ROLE_CUSTOMER);
        return role;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
        return role;
    }
}
