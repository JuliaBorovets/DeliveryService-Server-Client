package ua.training.api.dto;


import lombok.*;
import ua.training.domain.order.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReceiptDto {

    private Long id;

    private Long orderId;

    private BigDecimal priceInCents;

    private Status status;

    private Long userId;

    private Long bankCard;

    private LocalDate creationDate;
}
