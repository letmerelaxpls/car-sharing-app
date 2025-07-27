package csa.service.rental;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetARDDto;
import csa.exception.EntityNotFoundException;
import csa.exception.RentalException;
import csa.mapper.RentalMapper;
import csa.model.Car;
import csa.model.Rental;
import csa.repository.CarRepository;
import csa.repository.RentalRepository;
import csa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public RentalResponseDto findById(Long rentalId) {
        return rentalMapper.toDto(
                rentalRepository.findWithCarAndUserById(rentalId).orElseThrow(() ->
                        new EntityNotFoundException("Could not find Rental with id: "
                                + rentalId)));
    }

    @Override
    public RentalResponseDto save(RentalCreateRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(() ->
                new EntityNotFoundException("Could not find Car with id: "
                + requestDto.getCarId()));
        if (car.getInventory() < 1) {
            throw new RentalException("There are no available cars");
        }
        car.setInventory(car.getInventory() - 1);
        boolean userExists = userRepository.existsById(requestDto.getUserId());
        if (!userExists) {
            throw new EntityNotFoundException("Could not find User with id: "
                    + requestDto.getUserId());
        }
        return rentalMapper.toDto(
                rentalRepository.save(
                        rentalMapper.toModel(requestDto)));
    }

    @Transactional
    @Override
    public RentalResponseDto setActualReturnDate(Long rentalId, RentalSetARDDto requestDto) {
        Rental rental = rentalRepository.findWithCarById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Rental with id: " + rentalId));
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        rentalMapper.setActualReturnDate(rental, requestDto);
        rental.setIsActive(false);
        return rentalMapper.toDto(rental);
    }

    @Override
    public Page<RentalResponseDto> findByUserId(Long userId, Boolean isActive, Pageable pageable) {
        return rentalRepository.findAllByUserIdAndIsActive(userId, isActive, pageable)
                .map(rentalMapper::toDto);
    }
}
