package csa.service.telegram;

import csa.model.Rental;
import csa.repository.RentalRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Kiev")
    public void notifyAboutOverdueRentals() {
        List<Rental> rentals = rentalRepository
                .findAllByReturnDateLessThanAndIsActiveTrue(LocalDate.now()
                        .plusDays(1));
        if (rentals.isEmpty()) {
            notificationService.sendNoOverdueRentals();
            return;
        }
        notificationService.sendOverdueRentals(rentals);
    }
}
