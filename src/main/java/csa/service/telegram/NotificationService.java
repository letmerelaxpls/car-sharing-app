package csa.service.telegram;

import csa.model.Payment;
import csa.model.Rental;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface NotificationService {
    void sendNewRentalNotification(Rental rental);

    void sendReturnedRentalNotification(Rental rental);

    void sendSuccessfulPaymentNotification(Payment payment);

    void sendFailedPaymentNotification(Payment payment);

    SendMessage processMessage(Long chatId, String text);
}
