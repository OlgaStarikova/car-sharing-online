package com.example.carsharingonline.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Car response DTO")
public class CarDto {
    private String model;
    private String brand;
    private String carBodyType;
    private int inventory;
    private BigDecimal dailyFee;
}
