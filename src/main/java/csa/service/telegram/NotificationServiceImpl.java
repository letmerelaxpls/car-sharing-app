package csa.service.telegram;

import csa.dto.user.UserEmailRequestDto;
import csa.exception.TelegramProcessException;
import csa.model.Payment;
import csa.model.Rental;
import csa.model.User;
import csa.model.enums.RoleName;
import csa.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    private final UserRepository userRepository;
    private final Validator validator;
    @Value("${telegrambots.bots[0].token}")
    private String botToken;
    @Value("${admin.chat.id}")
    private String adminChatId;
    private final RestTemplate restTemplate;

    @Override
    public void sendNewRentalNotification(Rental rental) {
        String message = TelegramMessages.NEW_RENTAL.format(
                rental.getUser().getId(),
                rental.getCar().getModel(), rental.getCar().getBrand(),
                rental.getCar().getInventory());
        sendNotification(message);
    }

    @Override
    public void sendReturnedRentalNotification(Rental rental) {
        String message = TelegramMessages.RETURNED_RENTAL.format(
                rental.getUser().getId(),
                rental.getReturnDate(),
                rental.getActualReturnDate(),
                rental.getCar().getModel(),
                rental.getCar().getBrand(),
                rental.getCar().getInventory());
        sendNotification(message);
    }

    @Override
    public void sendSuccessfulPaymentNotification(Payment payment) {
        String message = TelegramMessages.SUCCESSFUL_PAYMENT.format(
                payment.getRental().getUser().getId(),
                payment.getType(),
                payment.getRental().getId(), payment.getRental().getRentalDate(),
                payment.getAmountToPay());
        sendNotification(message);
    }

    @Override
    public void sendFailedPaymentNotification(Payment payment) {
        String message = TelegramMessages.FAILED_PAYMENT.format(
                payment.getRental().getUser().getId(),
                payment.getType());
        sendNotification(message);
    }

    @Override
    public void sendCanceledPaymentNotification(Payment payment) {
        String message = TelegramMessages.CANCEL_PAYMENT.format(
                payment.getRental().getRentalDate());
        sendNotification(message);
    }

    @Override
    public void sendNoOverdueRentals() {
        sendNotification(TelegramMessages.NO_OVERDUE_RENTALS.getText());
    }

    @Override
    public void sendOverdueRentals(List<Rental> rentals) {
        StringBuilder builder = new StringBuilder(
                TelegramMessages.OVERDUE_RENTALS_HEADER
                .format(rentals.size()));
        rentals.forEach(rental -> {
            builder.append("\n").append(
                    TelegramMessages.OVERDUE_RENTALS.format(
                            rental.getUser().getId(),
                            rental.getCar().getModel(),
                            rental.getCar().getBrand(),
                            rental.getRentalDate(),
                            rental.getReturnDate()
                    ));
        });
        String message = builder.toString();
        sendNotification(message);
    }

    @Override
    public SendMessage processMessage(Long chatId, String text) {
        if (TelegramMessages.START_MESSAGE.getText().equalsIgnoreCase(text)) {
            return sendMessage(chatId, TelegramMessages.GREETING_MESSAGE.getText());
        } else {
            return processEmailMessage(chatId, text);
        }
    }

    private void sendNotification(String message) {
        String botUrl = TELEGRAM_API_URL + botToken + "/sendMessage";
        Map<String, String> params = Map.of(
                "chat_id", adminChatId,
                "text", message
        );
        try {
            restTemplate.postForEntity(botUrl, params, String.class);
        } catch (Exception e) {
            throw new TelegramProcessException("Could not send message to chat with id: "
                    + adminChatId, e);
        }
    }

    private SendMessage processEmailMessage(Long chatId, String text) {
        if (!isValidEmail(text)) {
            return sendMessage(chatId, TelegramMessages.INVALID_EMAIL_MESSAGE.getText());
        }
        Optional<User> optionalUser = userRepository.findByEmail(text);
        if (optionalUser.isEmpty()) {
            return sendMessage(chatId, TelegramMessages.EMAIL_NOT_EXIST.getText());
        }
        User user = optionalUser.get();
        if (user.getTelegramChatId() == null) {
            if (!hasAdminRole(user)) {
                return sendMessage(chatId, TelegramMessages.NOT_ALLOWED.getText());
            }
            user.setTelegramChatId(chatId.toString());
            userRepository.save(user);
            return sendMessage(chatId, TelegramMessages.VALID_EMAIL_MESSAGE.getText());
        }
        return sendMessage(chatId, TelegramMessages.ALREADY_REGISTERED.getText());
    }

    private SendMessage sendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN));
    }

    private boolean isValidEmail(String text) {
        UserEmailRequestDto email = new UserEmailRequestDto(text);
        Set<ConstraintViolation<UserEmailRequestDto>> violations =
                validator.validate(email);
        return violations.isEmpty();
    }
}
