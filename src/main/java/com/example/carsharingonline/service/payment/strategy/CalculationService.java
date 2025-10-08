package com.example.carsharingonline.service.payment.strategy;

import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import java.math.BigDecimal;

public interface CalculationService {
    BigDecimal calculateAmount(Rental rental);

    Payment.Type getServiceType();
}
