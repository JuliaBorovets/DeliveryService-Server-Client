package ua.training.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.training.api.dto.DestinationDto;
import ua.training.domain.order.Destination;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
class DestinationMapperTest {

    final Long ID = 1L;

    final String CITY_FROM = "city from";

    final String CITY_TO = "city to";

    final Long DAYS_TO_DELIVER = 12L;

    final BigDecimal PRICE_IN_CENTS = BigDecimal.valueOf(33);

    @Test
    void destinationToDestinationDto() {
        Destination destination = new Destination();
        destination.setId(ID);
        destination.setCityFrom(CITY_FROM);
        destination.setCityTo(CITY_TO);
        destination.setDaysToDeliver(DAYS_TO_DELIVER);
        destination.setPriceInCents(PRICE_IN_CENTS);

        DestinationDto destinationDto = DestinationMapper.INSTANCE.destinationToDestinationDto(destination);

        assertEquals(destinationDto.getId(), destination.getId());
        assertEquals(destinationDto.getCityFrom(), destination.getCityFrom());
        assertEquals(destinationDto.getCityTo(), destination.getCityTo());
        assertEquals(destinationDto.getDaysToDeliver(), destination.getDaysToDeliver());
        assertEquals(destinationDto.getPriceInCents(), destination.getPriceInCents());
    }
}
