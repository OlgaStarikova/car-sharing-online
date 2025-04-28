package com.example.carsharingonline.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateRentalRequestDto(
        @NotNull
        @FutureOrPresent
        LocalDateTime rentalDate,
        @NotNull
        @FutureOrPresent
        LocalDateTime returnDate,
        @Min(0)
        Long carId
) {
}
