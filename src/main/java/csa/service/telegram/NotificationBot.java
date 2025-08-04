package csa.service.telegram;

import csa.exception.TelegramProcessException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NotificationBot extends TelegramLongPollingBot {
    private final String botUsername;
    @Getter
    private final String botToken;
    private final NotificationService notificationService;

    public NotificationBot(@Value("${telegrambots.bots[0].username}")String username,
                           @Value("${telegrambots.bots[0].token}")String token,
                           NotificationService notificationService) {
        super(token);
        this.botUsername = username;
        this.botToken = token;
        this.notificationService = notificationService;
        System.out.println("Telegram bot started: " + botUsername);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage message = notificationService.processMessage(chatId, text);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new TelegramProcessException("Could not get response from TelegramAPI", e);
            }
        }
    }
}
