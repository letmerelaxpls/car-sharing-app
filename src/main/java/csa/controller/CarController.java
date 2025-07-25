package csa.controller;

import csa.dto.car.CarCreateRequestDto;
import csa.dto.car.CarInfoResponseDto;
import csa.dto.car.CarResponseDto;
import csa.service.car.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public Page<CarResponseDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{carId}")
    public CarInfoResponseDto getCarById(@PathVariable Long carId) {
        return carService.findById(carId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CarInfoResponseDto createCar(@RequestBody CarCreateRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @PutMapping("/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CarInfoResponseDto updateCar(@PathVariable Long carId,
                                        @RequestBody CarCreateRequestDto requestDto) {
        return carService.update(carId, requestDto);
    }

    @DeleteMapping("/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCar(@PathVariable Long carId) {
        carService.deleteById(carId);
    }
}
