package csa.controller;

import csa.dto.payment.PaymentDetailsDto;
import csa.dto.payment.PaymentRequestDto;
import csa.dto.payment.PaymentResponseDto;
import csa.dto.payment.PaymentSummaryDto;
import csa.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "Endpoints for managing payments")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "View user payments",
            description = "Endpoint for retrieving a list of payments of specific user")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PaymentSummaryDto> getUserPayments(
            @RequestParam("user_id") Long userId, Pageable pageable) {
        return paymentService.findByUserId(userId, pageable);
    }

    @Operation(summary = "View payment",
            description = "Endpoint for retrieving a specific payment")
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDetailsDto getById(@PathVariable Long paymentId) {
        return paymentService.findById(paymentId);
    }

    @Operation(summary = "Create session",
            description = "Endpoint for creating a session for rental payment")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createSession(Authentication authentication,
                                            @RequestBody @Valid PaymentRequestDto requestDto) {
        Long userId = (Long) authentication.getPrincipal();
        return paymentService.createSession(userId, requestDto);
    }

    @Operation(summary = "View success",
            description = "Endpoint for viewing a successful payment page")
    @GetMapping("/success")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public void paymentSuccess(@RequestParam("session_id") String sessionId) {
        paymentService.paymentSuccess(sessionId);
    }

    @Operation(summary = "View cancel",
            description = "Endpoint for viewing a canceled payment page")
    @GetMapping("/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public void paymentCancel(@RequestParam("session_id") String sessionId) {
        paymentService.paymentCancel(sessionId);
    }
}
