package com.example.carsharingonline.service.notification.telegram;

import com.example.carsharingonline.config.TelegramConfig;
import com.example.carsharingonline.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;
    private final TelegramConfig telegramConfig;

    @Override
    public void sendMessage(long chatId, String message) {
        log.debug("Preparing to send message to chatId={} with text='{}'", chatId, message);

        try {
            telegramBot.sendMessage(chatId, message);
            log.info("✅ Message sent successfully to chatId={}", chatId);
        } catch (Exception e) {
            log.error("❌ Failed to send message to chatId={}: {}", chatId, e.getMessage(), e);
        }
    }

    public void sendMessageAdmin(String message) {
        long adminChatId = telegramConfig.getChatId();
        log.debug("Sending message to admin chatId={} with text='{}'", adminChatId, message);

        try {
            telegramBot.sendMessage(adminChatId, message);
            log.info("📨 Message sent to admin (chatId={})", adminChatId);
        } catch (Exception e) {
            log.error("⚠️ Error sending message to admin (chatId={}): {}",
                    adminChatId, e.getMessage(), e);
        }
    }
}
