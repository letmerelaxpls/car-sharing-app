package csa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import csa.model.Payment;
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
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("findBySessionId should return correct Payment")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql",
            "classpath:database/payments/insert-2-payments.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/payments/delete-2-payments.sql",
            "classpath:database/rentals/delete-3-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findBySessionId_PaymentWithIdOne_True() {
        Long expectedId = 1L;
        String sessionId = "session_id";
        Payment result = paymentRepository.findBySessionId(sessionId).get();
        assertEquals(expectedId, result.getId());
    }

    @Test
    @DisplayName("findByRentalUserId should return correct payments")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql",
            "classpath:database/payments/insert-2-payments.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/payments/delete-2-payments.sql",
            "classpath:database/rentals/delete-3-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByRentalUserId_UserWithIdThree_True() {
        Long userId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> payments = paymentRepository.findByRentalUserId(userId, pageable);
        int expectedSize = 1;
        assertEquals(expectedSize, payments.getContent().size());
        assertThat(payments)
                .extracting(Payment::getId)
                .containsOnly(1L);
    }

    @Test
    @DisplayName("findByRentalIdAndRentalUserId should return correct Payment")
    @Sql(scripts = {"classpath:database/cars/insert-2-cars.sql",
            "classpath:database/users/insert-2-users.sql",
            "classpath:database/rentals/insert-3-rentals.sql",
            "classpath:database/payments/insert-2-payments.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/payments/delete-2-payments.sql",
            "classpath:database/rentals/delete-3-rentals.sql",
            "classpath:database/users/delete-2-users.sql",
            "classpath:database/cars/delete-2-cars.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByRentalIdAndRentalUserId_PaymentWithIdOne_True() {
        Long rentalId = 1L;
        Long userId = 3L;
        Payment result = paymentRepository
                .findByRentalIdAndRentalUserId(rentalId, userId).get();
        Long expectedPaymentId = 1L;
        assertEquals(expectedPaymentId, result.getId());
    }
}
