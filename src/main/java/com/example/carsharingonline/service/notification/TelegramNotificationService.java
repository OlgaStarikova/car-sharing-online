package com.example.carsharingonline.service.notification;

import com.example.carsharingonline.config.TelegramConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;
    private final TelegramConfig telegramconfig;

    @Override
    public void sendMessage(long chatId, String message) {
        telegramBot.sendMessage(chatId, message);
    }

    public void sendMessageAdmin(String message) {
        telegramBot.sendMessage(telegramconfig.getChatId(), message);
    }
}
