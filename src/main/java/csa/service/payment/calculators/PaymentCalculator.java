package csa.service.payment.calculators;

import csa.model.Rental;
import csa.model.enums.PaymentType;
import java.math.BigDecimal;

public interface PaymentCalculator {
    PaymentType getType();

    BigDecimal calculate(Rental rental);
}
