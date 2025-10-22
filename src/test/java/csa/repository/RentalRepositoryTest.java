package csa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import csa.model.Rental;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("findWithCarAndUserById should return Rental "
            + "with initialized Car And User")
    @Sql(scripts = {"classpath:database/cars/insert-2-cars.sql",
            "classpath:database/users/insert-2-users.sql",
            "classpath:database/rentals/insert-3-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/users/delete-2-users.sql",
            "classpath:database/cars/delete-2-cars.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findWithCarAndUserById_CarAndUserNotNull_True() {
        Long rentalId = 1L;
        Rental result = rentalRepository.findWithCarAndUserById(rentalId).get();

        assertNotNull(result.getCar());
        assertNotNull(result.getUser());
        Long expectedCarId = 4L;
        Long expectedUserId = 3L;
        assertEquals(expectedCarId, result.getCar().getId());
        assertEquals(expectedUserId, result.getUser().getId());
    }

    @Test
    @DisplayName("findAllByUserIdAndIsActive should return correct Rental")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByUserIdAndIsActive_RentalWithIdOne_True() {
        Long userId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rental> rentals = rentalRepository
                .findAllByUserIdAndIsActive(userId, true, pageable);
        int expectedSize = 1;
        assertEquals(expectedSize, rentals.getContent().size());
        assertThat(rentals)
                .extracting(Rental::getId)
                .containsOnly(1L);
    }

    @Test
    @DisplayName("findAllByReturnDateLessThanAndIsActiveTrue should return correct Rentals")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByReturnDateLessThanAndIsActiveTrue_TwoRentals_True() {
        LocalDate date = LocalDate.of(2025, 9, 2);
        List<Rental> rentals = rentalRepository
                .findAllByReturnDateLessThanAndIsActiveTrue(date.plusDays(1));
        int expectedSize = 2;
        assertEquals(expectedSize, rentals.size());
        assertThat(rentals)
                .extracting(Rental::getReturnDate)
                .allMatch(d -> !d.isAfter(date));
    }
}
