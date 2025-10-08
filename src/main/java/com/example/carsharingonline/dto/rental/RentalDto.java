package com.example.carsharingonline.dto.rental;

import com.example.carsharingonline.dto.car.CarDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Rental response DTO")
public record RentalDto(
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        CarDto carDto
) {
}
