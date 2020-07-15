package ua.training.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class DestinationDto {

    private Long id;

    private String cityFrom;

    private String cityTo;

    private Long daysToDeliver;

    private BigDecimal priceInCents;
}
