package csa.mapper;

import csa.config.MapperConfig;
import csa.dto.payment.PaymentDetailsDto;
import csa.dto.payment.PaymentRequestDto;
import csa.dto.payment.PaymentResponseDto;
import csa.dto.payment.PaymentSummaryDto;
import csa.model.Payment;
import csa.model.Rental;
import csa.model.enums.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);

    PaymentSummaryDto toSummaryDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDetailsDto toDetailsDto(Payment payment);

    @Mapping(target = "rental", ignore = true)
    Payment toModel(PaymentRequestDto requestDto);

    @AfterMapping
    default void finishToModelMapping(
            @MappingTarget Payment payment, PaymentRequestDto requestDto) {
        Rental rental = new Rental();
        rental.setId(requestDto.getRentalId());
        payment.setRental(rental);
        payment.setStatus(Status.PENDING);
    }
}
