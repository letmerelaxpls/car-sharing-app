package csa.service.rental;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetARDDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto findById(Long rentalId);

    RentalResponseDto save(RentalCreateRequestDto requestDto);

    RentalResponseDto setActualReturnDate(Long rentalId, RentalSetARDDto requestDto);

    Page<RentalResponseDto> findByUserId(
            Long userId, Boolean isActive, Pageable pageable);
}
