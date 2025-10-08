package com.example.carsharingonline.dto.rental;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateRentalRequestDto(
        @NotNull
        @FutureOrPresent
        LocalDate rentalDate,
        @NotNull
        @FutureOrPresent
        LocalDate returnDate,
        @Min(0)
        Long carId
) {
}
