package csa.service.telegram;

import lombok.Getter;

@Getter
public enum TelegramMessages {
    START_MESSAGE("/start"),
    GREETING_MESSAGE("Welcome to Car Sharing Notification Bot!\n"
            + "Please send your email to receive updates here!"),
    INVALID_EMAIL_MESSAGE("It doesn't look like email, please try again"),
    VALID_EMAIL_MESSAGE("Thanks!\nNow you will receive notifications here!"),
    ALREADY_REGISTERED("This email is already registered for notifications!"),
    EMAIL_NOT_EXIST("User with this email doesn't exist in Car Sharing App!"),
    NOT_ALLOWED("You can't use this bot if you are not ADMIN"),
    NEW_RENTAL("""
            New Rental has just been created!
            
            User id: %s
            Car model: %s
            Car brand: %s
            
            Remaining Car Inventory: %s"""),
    RETURNED_RENTAL("""
            Rental was returned!
            User id: %s
            Rental return date: %s
            Rental actual return date: %s
            Car model: %s
            Car brand: %s
            Remaining Car Inventory: %s"""),
    SUCCESSFUL_PAYMENT("""
            Payment Successful!
            User id: %s
            Payment type: %s
            Rental with id: %s, made on %s
            Amount to pay: %s"""),
    FAILED_PAYMENT("""
            Payment Failed!
            User id: %s
            Payment type: %s""");

    private final String text;
    TelegramMessages(String text) {
        this.text = text;
    }

    public String format(Object... args) {
        return String.format(text, args);
    }
}
