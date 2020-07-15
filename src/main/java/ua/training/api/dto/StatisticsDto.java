package ua.training.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@ToString
@Builder
@Getter
public class StatisticsDto {

    private BigDecimal earningsLastMonth;

    private BigDecimal earningsYear;

    private Long deliversNumber;

    private Long deliversNumberYear;

    private Map<Integer, Long> numberOfOrdersByForYear = new HashMap<>();

    private Map<Integer, BigDecimal> earningsOfOrdersByForYear = new HashMap<>();

}
