package csa.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalSetARDDto {
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate actualReturnDate;
}
