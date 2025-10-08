package com.example.carsharingonline.service.payment.strategy;

import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class FineCalculationService implements CalculationService {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.5");

    @Override
    public BigDecimal calculateAmount(Rental rental) {
        long overdueDays = ChronoUnit.DAYS.between(rental.getReturnDate(),
                rental.getActualReturnDate());
        return FINE_MULTIPLIER
                .multiply(rental.getCar().getDaylyFee())
                .multiply(BigDecimal.valueOf(overdueDays));
    }

    @Override
    public Payment.Type getServiceType() {
        return Payment.Type.FINE;
    }
}
