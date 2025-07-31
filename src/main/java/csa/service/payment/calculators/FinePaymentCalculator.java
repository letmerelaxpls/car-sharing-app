package csa.service.payment.calculators;

import csa.model.Rental;
import csa.model.enums.PaymentType;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinePaymentCalculator implements PaymentCalculator {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.5");
    private final BaseFeeCalculator baseFeeCalculator;

    @Override
    public PaymentType getType() {
        return PaymentType.FINE;
    }

    @Override
    public BigDecimal calculate(Rental rental) {
        long fineDays = ChronoUnit.DAYS.between(rental.getReturnDate(),
                rental.getActualReturnDate());
        return baseFeeCalculator.calculateBaseFee(rental)
                .add(rental.getCar().getDailyFee()
                .multiply(BigDecimal.valueOf(fineDays))
                .multiply(FINE_MULTIPLIER));
    }
}
