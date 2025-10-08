package com.example.carsharingonline.service.payment.provider.stripe;

import com.example.carsharingonline.service.payment.provider.stripe.builder.StripeUriBuilder;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${stripe.currency}")
    private String stripeCurrency;
    @Value("${stripe.quantity}")
    private long stripeQuantity;
    @Value("${stripe.name}")
    private String stripeName;
    @Value("${stripe.hour-in-seconds}")
    private long stripeHourInSeconds;

    public Session createSession(BigDecimal amount) {

        SessionCreateParams.LineItem.PriceData.ProductData productData
                = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(stripeName)
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams
                .LineItem.PriceData.builder()
                .setCurrency(stripeCurrency)
                .setUnitAmount(amount.longValue())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(stripeQuantity)
                .setPriceData(priceData)
                .build();

        String successUrl = StripeUriBuilder.buildSuccessUrl();
        String cancelUrl = StripeUriBuilder.buildCancelUrl();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setExpiresAt(Instant.now().getEpochSecond() + stripeHourInSeconds)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
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
