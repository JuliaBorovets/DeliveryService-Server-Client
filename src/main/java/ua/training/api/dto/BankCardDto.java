package ua.training.api.dto;


import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class BankCardDto {

    @EqualsAndHashCode.Include
    private Long id;

    @Min(1)
    @Max(12)
    private Long expMonth;

    @Min(2020)
    @Max(2040)
    private Long expYear;

    private Long ccv;

    private BigDecimal balance = BigDecimal.ZERO;
}
