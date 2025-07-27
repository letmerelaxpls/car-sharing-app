package csa.mapper;

import csa.config.MapperConfig;
import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import csa.model.Car;
import csa.model.Rental;
import csa.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "user", ignore = true)
    Rental toModel(RentalCreateRequestDto requestDto);

    @AfterMapping
    default void setCarsAndUsers(
            @MappingTarget Rental rental, RentalCreateRequestDto requestDto) {
        Car car = new Car();
        car.setId(requestDto.getCarId());
        User user = new User();
        user.setId(requestDto.getUserId());
        rental.setCar(car);
        rental.setUser(user);
    }

    @Mapping(target = "carId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    RentalResponseDto toDto(Rental rental);

    @AfterMapping
    default void setCarAndUserIds(
            @MappingTarget RentalResponseDto responseDto, Rental rental) {
        responseDto.setCarId(rental.getCar().getId());
        responseDto.setUserId(rental.getUser().getId());
    }

    void setActualReturnDate(@MappingTarget Rental rental, RentalSetActualReturnDateDto requestDto);
}
