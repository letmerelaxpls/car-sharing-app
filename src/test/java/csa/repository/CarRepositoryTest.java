package csa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {
    @Autowired
    private CarRepository carRepository;

    @Test
    @DisplayName("existsById should return correct value")
    @Sql(scripts = "classpath:database/cars/insert-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsById_CarExists_True() {
        Long carId = 4L;
        Long fakeCarId = 99L;
        boolean firstResult = carRepository.existsById(carId);
        boolean secondResult = carRepository.existsById(fakeCarId);
        boolean expected = true;

        assertEquals(expected, firstResult);
        assertEquals(!expected, secondResult);
    }
}
