package csa.service.telegram;

import static csa.util.PaymentTestUtil.createPayment;
import static csa.util.RentalTestUtil.createRental;
import static csa.util.UserTestUtil.createAdminUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import csa.dto.user.UserEmailRequestDto;
import csa.model.Payment;
import csa.model.Rental;
import csa.model.User;
import csa.repository.UserRepository;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    private static final String TELEGRAM_API_URL =
            "https://api.telegram.org/botTEST_TOKEN/sendMessage";
    private final String adminChatId = "TEST_ID";
    @Mock
    private UserRepository userRepository;
    @Mock
    private Validator validator;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        String botToken = "TEST_TOKEN";

        ReflectionTestUtils.setField(notificationService, "botToken", botToken);
        ReflectionTestUtils.setField(notificationService, "adminChatId", adminChatId);
    }

    @Test
    @DisplayName("sendNewRentalNotification should call Telegram API")
    void sendNewRentalNotification_CorrectData_True() {
        Rental rental = createRental();

        notificationService.sendNewRentalNotification(rental);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                any(Map.class),
                eq(String.class));
    }

    @Test
    @DisplayName("sendReturnedRentalNotification should call Telegram API")
    void sendReturnedRentalNotification_CorrectData_True() {
        Rental rental = createRental();

        notificationService.sendReturnedRentalNotification(rental);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                any(Map.class),
                eq(String.class));
    }

    @Test
    @DisplayName("sendSuccessfulPaymentNotification should call Telegram API")
    void sendSuccessfulPaymentNotification_CorrectData_True() {
        Payment payment = createPayment();

        notificationService.sendSuccessfulPaymentNotification(payment);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                any(Map.class),
                eq(String.class));
    }

    @Test
    @DisplayName("sendFailedPaymentNotification should call Telegram API")
    void sendFailedPaymentNotification_CorrectData_True() {
        Payment payment = createPayment();

        notificationService.sendFailedPaymentNotification(payment);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                any(Map.class),
                eq(String.class));
    }

    @Test
    @DisplayName("sendCanceledPaymentNotification should call Telegram API")
    void sendCanceledPaymentNotification_CorrectData_True() {
        Payment payment = createPayment();

        notificationService.sendCanceledPaymentNotification(payment);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                any(Map.class),
                eq(String.class));
    }

    @Test
    @DisplayName("sendNoOverdueRentals should contain correct message")
    void sendNoOverdueRentals_CorrectMessage_True() {
        notificationService.sendNoOverdueRentals();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                captor.capture(),
                eq(String.class));
        Map<String, String> params = captor.getValue();
        assertEquals(TelegramMessages.NO_OVERDUE_RENTALS.getText(), params.get("text"));

    }

    @Test
    @DisplayName("sendOverdueRentals should call Telegram API")
    void sendOverdueRentals_CorrectData_True() {
        List<Rental> rentalList = List.of(createRental());

        notificationService.sendOverdueRentals(rentalList);

        verify(restTemplate).postForEntity(
                eq(TELEGRAM_API_URL),
                any(Map.class),
                eq(String.class));
    }

    @Test
    @DisplayName("processMessage should return greeting message")
    void processMessage_CorrectMessage_True() {
        SendMessage result = notificationService.processMessage(
                3L, TelegramMessages.START_MESSAGE.getText());

        assertEquals(TelegramMessages.GREETING_MESSAGE.getText(), result.getText());
    }

    @Test
    @DisplayName("processEmailMessage should save user when email is valid")
    void processEmailMessage_ValidEmail_True() {
        User user = createAdminUser();
        Long chatId = 4L;

        when(validator.validate(any(UserEmailRequestDto.class))).thenReturn(Collections.emptySet());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        SendMessage result = notificationService.processMessage(chatId, user.getEmail());

        assertEquals(TelegramMessages.VALID_EMAIL_MESSAGE.getText(), result.getText());
        verify(validator).validate(any(UserEmailRequestDto.class));
        verify(userRepository).findByEmail(user.getEmail());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(chatId.toString(), captor.getValue().getTelegramChatId());
    }
}
