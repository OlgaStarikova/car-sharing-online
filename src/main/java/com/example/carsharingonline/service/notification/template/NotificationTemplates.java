package com.example.carsharingonline.service.notification.template;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotificationTemplates {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getTemplate(NotificationType type, Object... params) {
        return switch (type) {
            case NEW_RENTAL -> formatNewRental(
                    (Long) params[0],
                    (String) params[1],
                    (String) params[2],
                    (LocalDate) params[3],
                    (LocalDate) params[4]
            );
            case PAYMENT_SUCCESS -> formatPaymentSuccess(
                    (Long) params[0],
                    (BigDecimal) params[1],
                    (Long) params[2]
            );
            case PAYMENT_FAILED -> formatPaymentFailed(
                    (Long) params[0],
                    (Long) params[1]
            );
            case OVERDUE_RENTAL -> formatOverdueRental(
                    (Long) params[0],
                    (String) params[1],
                    (String) params[2],
                    (String) params[3],
                    (LocalDate) params[4],
                    (Long) params[5]
            );
        };
    }

    private static String formatNewRental(Long carId, String carModel, String userEmail,
                                          LocalDate rentalDate, LocalDate returnDate) {
        return """
                🚗 *New Rental Created* 🚗
                CarId: %d
                CarModel: %s
                UserEmail: %s
                Rental Date: %s
                Return Date: %s
                """.formatted(carId, carModel, userEmail,
                rentalDate.format(DATE_FORMATTER),
                returnDate.format(DATE_FORMATTER));
    }

    private static String formatPaymentSuccess(Long paymentId, BigDecimal amount, Long rentalId) {
        return """
                💰 *Payment Successful* 💰
                Payment ID: %d
                Amount: %s
                For Rental: %d
                """.formatted(paymentId, amount, rentalId);
    }

    private static String formatOverdueRental(Long carId, String carModel, String userName,
                                              String userEmail, LocalDate dateForReturn,
                                              Long overduePeriod) {
        return """
                ⚠️ *Overdue Rental* ⚠️
                carID: %d
                User Name: %s
                User Email: %s
                Car: %s
                Date for return: %s
                Overdue: %d days
                """.formatted(carId, carModel, userName, userEmail,
                dateForReturn.format(DATE_FORMATTER), overduePeriod);
    }

    private static String formatPaymentFailed(Long paymentId, Long rentalId) {
        return """
                ❌ *Payment Failed* ❌
                Payment ID: %d
                Rental ID: %d
                """.formatted(paymentId, rentalId);
    }
}
