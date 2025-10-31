package csa.service.payment.calculators;

import csa.model.Rental;
import csa.model.enums.PaymentType;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class RegularPaymentCalculator implements PaymentCalculator {
    private final BaseFeeCalculator baseFeeCalculator;

    @Override
    public PaymentType getType() {
        return PaymentType.PAYMENT;
    }

    @Override
    public BigDecimal calculate(Rental rental) {
        BigDecimal fullRentalAmount = baseFeeCalculator.calculateBaseFee(rental);
        long days = ChronoUnit.DAYS.between(rental.getActualReturnDate(), rental.getReturnDate());
        BigDecimal regularAmount = rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(days));
        return fullRentalAmount.subtract(regularAmount);
    }
}
