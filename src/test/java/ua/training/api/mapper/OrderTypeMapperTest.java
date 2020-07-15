package ua.training.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.training.api.dto.OrderTypeDto;
import ua.training.domain.order.OrderType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class OrderTypeMapperTest {

    final Long ID = 2L;

    final String NAME = "type";

    final BigDecimal PRICE_IN_CENTS = BigDecimal.valueOf(22);

    @Test
    void orderTypeToOrderTypeDto() {

        OrderType orderType = new OrderType();
        orderType.setId(ID);
        orderType.setName(NAME);
        orderType.setPriceInCents(PRICE_IN_CENTS);

        OrderTypeDto orderTypeDto = OrderTypeMapper.INSTANCE.orderTypeToOrderTypeDto(orderType);

        assertEquals(orderTypeDto.getId(), orderType.getId());
        assertEquals(orderTypeDto.getName(), orderType.getName());
        assertEquals(orderTypeDto.getPriceInCents(), orderType.getPriceInCents());
    }
}
