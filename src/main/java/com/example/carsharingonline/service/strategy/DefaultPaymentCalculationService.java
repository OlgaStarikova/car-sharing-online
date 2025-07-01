package com.example.carsharingonline.service.strategy;

import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class DefaultPaymentCalculationService implements CalculationService {

    @Override
    public BigDecimal calculateAmount(Rental rental) {
        long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return rental.getCar().getDaylyFee().multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public Payment.Type getServiceType() {
        return Payment.Type.PAYMENT;
    }
}
