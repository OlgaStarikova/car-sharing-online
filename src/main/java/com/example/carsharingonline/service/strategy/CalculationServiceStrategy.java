package com.example.carsharingonline.service.strategy;

import com.example.carsharingonline.model.Payment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculationServiceStrategy {
    private final List<CalculationService> calculationServices;

    public CalculationService getCalculationService(Payment.Type paymentType) {
        return calculationServices.stream()
                .filter(service -> service.getServiceType() == paymentType)
                .findFirst()
                .orElseThrow(()
                        -> new IllegalArgumentException("No CalculationService found for type: "
                        + paymentType));
    }
}
