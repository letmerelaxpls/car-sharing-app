package csa.util;

import static csa.util.CarTestUtil.createCar;
import static csa.util.UserTestUtil.createCustomerUser;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import csa.model.Rental;
import java.time.LocalDate;

public class RentalTestUtil {
    public static Rental createRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2025, 8, 1));
        rental.setReturnDate(LocalDate.of(2025, 9, 1));
        rental.setActualReturnDate(LocalDate.of(2025, 9, 1));
        rental.setCar(createCar());
        rental.setUser(createCustomerUser());
        return rental;
    }

    public static RentalResponseDto createFirstRentalResponseDto() {
        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setId(1L);
        responseDto.setRentalDate(LocalDate.of(2025, 8, 1));
        responseDto.setReturnDate(LocalDate.of(2025, 9, 1));
        responseDto.setActualReturnDate(LocalDate.of(2025, 9, 1));
        responseDto.setCarId(4L);
        responseDto.setUserId(3L);
        return responseDto;
    }

    public static RentalCreateRequestDto createRentalRequestDto() {
        RentalCreateRequestDto requestDto = new RentalCreateRequestDto();
        requestDto.setRentalDate(LocalDate.of(2025, 8, 1));
        requestDto.setReturnDate(LocalDate.of(2025, 9, 1));
        requestDto.setCarId(4L);
        return requestDto;
    }

    public static RentalSetActualReturnDateDto createActualReturnDateDto() {
        RentalSetActualReturnDateDto actualReturnDateDto = new RentalSetActualReturnDateDto();
        actualReturnDateDto.setActualReturnDate(LocalDate.of(2025, 9, 1));
        return actualReturnDateDto;
    }
}
