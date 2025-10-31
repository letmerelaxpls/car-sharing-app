package csa.dto.payment;

import csa.model.enums.PaymentType;
import csa.model.enums.Status;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentSummaryDto {
    private Long id;
    private Status status;
    private PaymentType type;
    private BigDecimal amountToPay;
}
