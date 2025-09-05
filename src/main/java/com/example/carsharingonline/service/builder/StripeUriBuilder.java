package com.example.carsharingonline.service.builder;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

public class StripeUriBuilder {

    public static final String SUCCESS_PATH = "/registered/payments/success";
    public static final String CANCEL_PATH = "/registered/payments/cancel";

    public static String buildSuccessUrl() {
        String baseUrl = extractBaseUrl();
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(SUCCESS_PATH)
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .toUriString();
    }

    public static String buildCancelUrl() {
        String baseUrl = extractBaseUrl();
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(CANCEL_PATH)
                .toUriString();
    }

    private static String extractBaseUrl() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        return UriComponentsBuilder
                .fromHttpUrl(request.getRequestURL().toString())
                .replacePath(null)
                .replaceQuery(null)
                .toUriString();
    }
}
