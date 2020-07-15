package ua.training.api.dto;


import lombok.*;
import ua.training.domain.order.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    private Long id;

    @Size(min = 3, max = 255)
    private String description;

    @NotBlank
    private String destinationCityFrom;

    @NotBlank
    private String destinationCityTo;

    @NotBlank
    private String type;

    @Positive
    private BigDecimal weight;

    private Status status = Status.NOT_PAID;

    private String shippingDate;

    private String deliveryDate;

    private BigDecimal shippingPriceInCents;

    private ReceiptDto receipt;

    private OrderTypeDto orderType;

    private DestinationDto destination;

}
