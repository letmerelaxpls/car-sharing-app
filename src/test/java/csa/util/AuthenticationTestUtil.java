package csa.util;

import static csa.util.UserTestUtil.createUser;

import csa.model.Role;
import csa.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationTestUtil {
    public static Authentication createAuthentication(Long userId, Role role) {
        User user = createUser(userId, role);
        return new UsernamePasswordAuthenticationToken(
                userId, null, user.getAuthorities());
    }
}
