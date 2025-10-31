package csa.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalSetActualReturnDateDto {
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate actualReturnDate;
}
