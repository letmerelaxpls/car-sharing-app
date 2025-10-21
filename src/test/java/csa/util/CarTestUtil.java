package csa.util;

import csa.dto.car.CarCreateRequestDto;
import csa.dto.car.CarInfoResponseDto;
import csa.dto.car.CarResponseDto;
import csa.model.Car;
import csa.model.enums.CarType;
import java.math.BigDecimal;

public class CarTestUtil {
    public static Car createCar() {
        Car car = new Car();
        car.setId(4L);
        car.setModel("Civic");
        car.setBrand("Honda");
        car.setType(CarType.SEDAN);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(100));
        return car;
    }

    public static CarResponseDto createCarResponseDto() {
        CarResponseDto carResponseDto = new CarResponseDto();
        carResponseDto.setModel("baseModel");
        carResponseDto.setBrand("baseBrand");
        carResponseDto.setDailyFee(BigDecimal.valueOf(100));
        return carResponseDto;
    }

    public static CarInfoResponseDto createCarInfoResponseDto() {
        CarInfoResponseDto carInfoResponseDto = new CarInfoResponseDto();
        carInfoResponseDto.setModel("Civic");
        carInfoResponseDto.setBrand("Honda");
        carInfoResponseDto.setType("SEDAN");
        carInfoResponseDto.setInventory(5);
        carInfoResponseDto.setDailyFee(BigDecimal.valueOf(100));
        return carInfoResponseDto;
    }

    public static CarCreateRequestDto createCarCreateRequestDto() {
        CarCreateRequestDto carCreateRequestDto = new CarCreateRequestDto();
        carCreateRequestDto.setModel("Civic");
        carCreateRequestDto.setBrand("Honda");
        carCreateRequestDto.setType(CarType.SEDAN);
        carCreateRequestDto.setInventory(5);
        carCreateRequestDto.setDailyFee(BigDecimal.valueOf(100));
        return carCreateRequestDto;
    }
}
