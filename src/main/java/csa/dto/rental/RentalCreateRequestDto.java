package csa.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalCreateRequestDto {
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate rentalDate;
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate returnDate;
    @NotNull
    @Positive
    private Long carId;
}
