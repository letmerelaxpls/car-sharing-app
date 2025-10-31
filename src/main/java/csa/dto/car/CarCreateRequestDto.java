package csa.dto.car;

import csa.model.enums.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarCreateRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotBlank
    private CarType type;
    private int inventory;
    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
