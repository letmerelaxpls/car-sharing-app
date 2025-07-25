package csa.mapper;

import csa.config.MapperConfig;
import csa.dto.car.CarCreateRequestDto;
import csa.dto.car.CarInfoResponseDto;
import csa.dto.car.CarResponseDto;
import csa.model.Car;
import csa.model.enums.CarType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toDto(Car car);

    Car toModel(CarCreateRequestDto requestDto);

    @Mapping(target = "type", source = "type", qualifiedByName = "carType")
    CarInfoResponseDto toInfoDto(Car car);

    @Named("carType")
    default String carType(CarType carType) {
        return carType.name();
    }

    void updateFromDto(@MappingTarget Car car, CarCreateRequestDto requestDto);
}
