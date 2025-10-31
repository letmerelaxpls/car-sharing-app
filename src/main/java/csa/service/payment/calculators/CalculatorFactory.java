package csa.service.payment.calculators;

import csa.model.enums.PaymentType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorFactory {
    private final Map<PaymentType, PaymentCalculator> calculators;

    @Autowired
    public CalculatorFactory(List<PaymentCalculator> strategies) {
        calculators = strategies.stream()
                .collect(Collectors.toMap(PaymentCalculator::getType, Function.identity()));
    }

    public PaymentCalculator getCalculator(PaymentType type) {
        return calculators.get(type);
    }
}
