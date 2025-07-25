package csa.service.car;

import csa.dto.car.CarCreateRequestDto;
import csa.dto.car.CarInfoResponseDto;
import csa.dto.car.CarResponseDto;
import csa.exception.EntityNotFoundException;
import csa.mapper.CarMapper;
import csa.model.Car;
import csa.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toDto);
    }

    @Override
    public CarInfoResponseDto findById(Long carId) {
        return carMapper.toInfoDto(carRepository.findById(carId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Could not find car by id: "
                        + carId)));
    }

    @Override
    public CarInfoResponseDto save(CarCreateRequestDto requestDto) {
        return carMapper.toInfoDto(
                carRepository.save(
                        carMapper.toModel(requestDto)));
    }

    @Override
    public CarInfoResponseDto update(Long carId, CarCreateRequestDto requestDto) {
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Car with id: " + carId));
        carMapper.updateFromDto(car, requestDto);
        return carMapper.toInfoDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long carId) {
        carRepository.deleteById(carId);
    }
}
