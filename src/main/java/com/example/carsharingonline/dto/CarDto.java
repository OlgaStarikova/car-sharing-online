package com.example.carsharingonline.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Car response DTO")
public record CarDto(
        String model,
        String brand,
        String carBodyType,
        int inventory,
        BigDecimal daylyFee
) {
}

