package csa.service.rental;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import csa.exception.EntityNotFoundException;
import csa.exception.RentalException;
import csa.mapper.RentalMapper;
import csa.model.Car;
import csa.model.Rental;
import csa.model.User;
import csa.repository.CarRepository;
import csa.repository.RentalRepository;
import csa.repository.UserRepository;
import csa.service.telegram.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RentalResponseDto findById(Long authUserId, Long rentalId, boolean isAdmin) {
        Rental rental = rentalRepository.findWithCarAndUserById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Rental with id: "
                        + rentalId));
        if (!isAdmin && !rental.getUser().getId().equals(authUserId)) {
            throw new RentalException("You don`t have permission to view this rental.");
        }
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto save(RentalCreateRequestDto requestDto, Long userId) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(() ->
                new EntityNotFoundException("Could not find Car with id: "
                + requestDto.getCarId()));
        if (car.getInventory() < 1) {
            throw new RentalException("There are no available cars");
        }
        car.setInventory(car.getInventory() - 1);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find User with id: "
                        + userId));
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setCar(car);
        rental.setUser(user);
        rentalRepository.save(rental);
        notificationService.sendNewRentalNotification(rental);
        return rentalMapper.toDto(rental);
    }

    @Transactional
    @Override
    public RentalResponseDto setActualReturnDate(
            Long rentalId, RentalSetActualReturnDateDto requestDto) {
        Rental rental = rentalRepository.findWithCarById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Rental with id: " + rentalId));
        if (!rental.getIsActive()) {
            throw new RentalException("The rental is already closed!");
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        rentalMapper.setActualReturnDate(rental, requestDto);
        rental.setIsActive(false);
        rentalRepository.save(rental);
        notificationService.sendReturnedRentalNotification(rental);
        return rentalMapper.toDto(rental);
    }

    @Override
    public Page<RentalResponseDto> findByUserId(Long authUserId, boolean isAdmin,
                                                Long userId, Boolean isActive, Pageable pageable) {
        if (!isAdmin && !authUserId.equals(userId)) {
            throw new RentalException("You don`t have permission to view these rentals.");
        }
        return rentalRepository
                .findAllByUserIdAndIsActive(userId, isActive, pageable).map(rentalMapper::toDto);
    }
}
