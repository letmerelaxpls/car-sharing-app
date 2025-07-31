package csa.dto.payment;

import csa.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotNull
    private Long rentalId;
    @NotNull
    private PaymentType type;
}
