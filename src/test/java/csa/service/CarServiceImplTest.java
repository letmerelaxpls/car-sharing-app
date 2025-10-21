package csa.service;

import static csa.util.CarTestUtil.createCar;
import static csa.util.CarTestUtil.createCarCreateRequestDto;
import static csa.util.CarTestUtil.createCarInfoResponseDto;
import static csa.util.CarTestUtil.createCarResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import csa.dto.car.CarCreateRequestDto;
import csa.dto.car.CarInfoResponseDto;
import csa.dto.car.CarResponseDto;
import csa.mapper.CarMapper;
import csa.model.Car;
import csa.repository.CarRepository;
import csa.service.car.CarServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("findAll should return correct amount of cars")
    void findAll_TwoCars_True() {
        Car firstCar = createCar();
        Car secondCar = createCar();
        List<Car> cars = List.of(firstCar, secondCar);
        CarResponseDto firstDto = createCarResponseDto();
        CarResponseDto secondDto = createCarResponseDto();
        List<CarResponseDto> dtos = List.of(firstDto, secondDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(cars, pageable, 2);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(cars.get(0))).thenReturn(dtos.get(0));
        when(carMapper.toDto(cars.get(1))).thenReturn(dtos.get(1));
        Page<CarResponseDto> result = carService.findAll(pageable);

        Page<CarResponseDto> expected = new PageImpl<>(dtos, pageable, 2);
        assertEquals(expected, result);
        verify(carRepository).findAll(pageable);
        verify(carMapper).toDto(cars.get(0));
        verify(carMapper).toDto(cars.get(1));
    }

    @Test
    @DisplayName("findById should return correct Car")
    void findById_CarWithIdOne_True() {
        Long id = 1L;
        Car car = createCar();
        car.setId(id);
        CarInfoResponseDto expected = createCarInfoResponseDto();
        expected.setId(id);

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carMapper.toInfoDto(car)).thenReturn(expected);
        CarInfoResponseDto result = carService.findById(id);

        assertEquals(expected, result);
        verify(carRepository).findById(id);
        verify(carMapper).toInfoDto(car);
    }

    @Test
    @DisplayName("save should return correct Car dto")
    void save_CarInfoResponseDtoWithIdOne_True() {
        CarCreateRequestDto createDto = createCarCreateRequestDto();
        Car car = createCar();
        CarInfoResponseDto expected = createCarInfoResponseDto();

        when(carMapper.toModel(createDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toInfoDto(car)).thenReturn(expected);
        CarInfoResponseDto result = carService.save(createDto);

        assertEquals(expected, result);
        verify(carMapper).toModel(createDto);
        verify(carRepository).save(car);
        verify(carMapper).toInfoDto(car);
    }

    @Test
    @DisplayName("update should return correct Car dto")
    void update_CarCreateRequestDtoChangeModel_True() {
        Long id = 1L;
        Car car = createCar();
        Car changedCar = createCar();
        String newModel = "newModel";
        changedCar.setModel(newModel);
        CarCreateRequestDto requestDto = createCarCreateRequestDto();
        requestDto.setModel(newModel);
        CarInfoResponseDto expected = createCarInfoResponseDto();
        expected.setModel(newModel);

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        doNothing().when(carMapper).updateFromDto(car, requestDto);
        when(carRepository.save(car)).thenReturn(changedCar);
        when(carMapper.toInfoDto(changedCar)).thenReturn(expected);
        CarInfoResponseDto result = carService.update(id, requestDto);

        assertEquals(expected, result);
        verify(carRepository).findById(id);
        verify(carMapper).updateFromDto(car, requestDto);
        verify(carRepository).save(car);
        verify(carMapper).toInfoDto(changedCar);
    }

    @Test
    @DisplayName("delete should remove correct car")
    void delete_CarWithIdOne_True() {
        Long id = 1L;

        carService.deleteById(id);

        verify(carRepository).deleteById(id);
    }

}