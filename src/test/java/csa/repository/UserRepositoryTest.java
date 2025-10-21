package csa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import csa.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("existsByEmail should return correct value")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByEmail_UserExists_True() {
        String email = "test@gmail.com";
        String fakeEmail = "fake@gmail.com";
        boolean firstResult = userRepository.existsByEmail(email);
        boolean secondResult = userRepository.existsByEmail(fakeEmail);
        boolean expected = true;
        assertEquals(expected, firstResult);
        assertEquals(!expected, secondResult);
    }

    @Test
    @DisplayName("findWithRolesById should return User with initialized Roles")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findWithRolesById_RolesNotNull_True() {
        Long userId = 3L;
        User user = userRepository.findWithRolesById(userId).get();
        assertNotNull(user.getRoles());
    }
}
