package csa.controller;

import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import csa.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "View rental",
            description = "Endpoint for retrieving a specific rental")
    @GetMapping("/{rentalId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public RentalResponseDto getRentalById(Authentication authentication,
                                           @PathVariable Long rentalId) {
        Long authUserId = (Long) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        return rentalService.findById(authUserId, rentalId, isAdmin);
    }

    @Operation(summary = "Add rental",
            description = "Endpoint for creating a new rental")
    @PostMapping
    public RentalResponseDto addRental(Authentication authentication,
            @RequestBody @Valid RentalCreateRequestDto requestDto) {
        Long userId = (Long) authentication.getPrincipal();
        return rentalService.save(requestDto, userId);
    }

    @Operation(summary = "Set actual return date",
            description = "Endpoint for setting a specific rental`s actual return date")
    @PostMapping("/{rentalId}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public RentalResponseDto setActualReturnDate(
            @PathVariable Long rentalId,
            @RequestBody @Valid RentalSetActualReturnDateDto requestDto) {
        return rentalService.setActualReturnDate(rentalId, requestDto);
    }

    @Operation(summary = "View user rentals",
            description = "Endpoint for retrieving a list of rentals of specific user")
    @GetMapping("/byUser/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public Page<RentalResponseDto> getRentalByUserId(Authentication authentication,
                                                     @PathVariable Long userId,
                                                     @RequestParam Boolean isActive,
                                                     Pageable pageable) {
        Long authUserId = (Long) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        return rentalService.findByUserId(authUserId, isAdmin, userId, isActive, pageable);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(r ->
                        r.getAuthority().equals("ROLE_ADMIN"));
    }
}
