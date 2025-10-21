package csa.service.telegram;


import csa.model.Rental;
import csa.repository.RentalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static csa.util.RentalTestUtil.createRental;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @Test
    @DisplayName("notifyAboutOverdueRentals should call sendNoOverdueRentals when no overdue Rentals ")
    void notifyAboutOverdueRentals_NoOverdueRentals_True() {
        when(rentalRepository.findAllByReturnDateLessThanAndIsActiveTrue(any()))
                .thenReturn(Collections.emptyList());

        notificationScheduler.notifyAboutOverdueRentals();

        verify(rentalRepository).findAllByReturnDateLessThanAndIsActiveTrue(any());
        verify(notificationService).sendNoOverdueRentals();
        verify(notificationService, never()).sendOverdueRentals(any());
    }

    @Test
    @DisplayName("notifyAboutOverdueRentals should call sendOverdueRentals when overdue Rentals exist")
    void notifyAboutOverdueRentals_OverdueRentals_True() {
        List<Rental> rentals = List.of(createRental());

        when(rentalRepository.findAllByReturnDateLessThanAndIsActiveTrue(any()))
                .thenReturn(rentals);

        notificationScheduler.notifyAboutOverdueRentals();

        verify(rentalRepository).findAllByReturnDateLessThanAndIsActiveTrue(any());
        verify(notificationService).sendOverdueRentals(rentals);
        verify(notificationService, never()).sendNoOverdueRentals();
    }

}