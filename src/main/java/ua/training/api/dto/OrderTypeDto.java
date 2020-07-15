package ua.training.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderTypeDto {

    private Long id;

    private String name;

    private BigDecimal priceInCents;

}
