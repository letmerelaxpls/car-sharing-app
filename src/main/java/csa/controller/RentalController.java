package csa.controller;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import csa.service.rental.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/{rentalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public RentalResponseDto getRentalById(@PathVariable Long rentalId) {
        return rentalService.findById(rentalId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RentalResponseDto addRental(
            @RequestBody @Valid RentalCreateRequestDto requestDto) {
        return rentalService.save(requestDto);
    }

    @PostMapping("/{rentalId}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public RentalResponseDto setActualReturnDate(
            @PathVariable Long rentalId,
            @RequestBody @Valid RentalSetActualReturnDateDto requestDto) {
        return rentalService.setActualReturnDate(rentalId, requestDto);
    }

    @GetMapping("/byUser/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<RentalResponseDto> getRentalByUserId(
            @PathVariable Long userId, @RequestParam Boolean isActive, Pageable pageable) {
        return rentalService.findByUserId(userId, isActive, pageable);
    }
}
