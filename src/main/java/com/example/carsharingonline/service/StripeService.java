package com.example.carsharingonline.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    private static final String CURRENCY = "USD";
    private static final long QUANTITY = 1L;
    private static final String NAME = "Car rental stripe payment";
    private static final String SUCCESS_URL = "http://localhost:8088/registered/payments/success?sessionId={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL = "http://localhost:8088/registered/payments/cancel";
    private static final long HOUR_IN_SECONDS = 7200;

    public Session createSession(BigDecimal amount) {
        SessionCreateParams.LineItem.PriceData.ProductData productData
                = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(NAME)
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams
                .LineItem.PriceData.builder()
                .setCurrency(CURRENCY)
                .setUnitAmount(amount.longValue())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(QUANTITY)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setExpiresAt(Instant.now().getEpochSecond() + HOUR_IN_SECONDS)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(lineItem)
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            // change to custom exception
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return session;
    }
}
