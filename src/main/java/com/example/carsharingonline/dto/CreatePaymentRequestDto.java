package com.example.carsharingonline.dto;

import com.example.carsharingonline.model.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentRequestDto {
    @Positive
    private Long rentalId;
    @NotNull
    private Payment.Type type;
}
