package csa.service.rental;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto findById(Long authUserId, Long rentalId, boolean isAdmin);

    RentalResponseDto save(RentalCreateRequestDto requestDto, Long userId);

    RentalResponseDto setActualReturnDate(Long rentalId, RentalSetActualReturnDateDto requestDto);

    Page<RentalResponseDto> findByUserId(Long authUserId, boolean isAdmin,
                                         Long userId, Boolean isActive, Pageable pageable);
}
