package com.example.carsharingonline.dto.payment;

import com.example.carsharingonline.model.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequestDto(
        @Positive
        Long rentalId,
        @NotNull
        Payment.Type type
) {
}


