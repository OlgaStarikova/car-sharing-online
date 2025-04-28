package com.example.carsharingonline.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Rental response DTO")
public record RentalDto(
        LocalDateTime rentalDate,
        LocalDateTime returnDate,
        LocalDateTime actualReturnDate,
        CarDto carDto
) {
}
