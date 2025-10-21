package csa.service.telegram;

import csa.model.Payment;
import csa.model.Rental;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface NotificationService {
    void sendNewRentalNotification(Rental rental);

    void sendReturnedRentalNotification(Rental rental);

    void sendSuccessfulPaymentNotification(Payment payment);

    void sendFailedPaymentNotification(Payment payment);

    void sendCanceledPaymentNotification(Payment payment);

    void sendNoOverdueRentals();

    void sendOverdueRentals(List<Rental> rentals);

    SendMessage processMessage(Long chatId, String text);
}
