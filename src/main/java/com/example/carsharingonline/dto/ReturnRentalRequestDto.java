package com.example.carsharingonline.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReturnRentalRequestDto(
        @NotNull
        @FutureOrPresent
        LocalDate actualReturnDate,
        @Min(0)
        Long carId
) {
}
