package ua.training.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.training.api.dto.DestinationDto;
import ua.training.api.dto.OrderDto;
import ua.training.api.dto.OrderTypeDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.domain.order.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    final Long ID = 1L;

    final String DESCRIPTION = "description";

    final String DESTINATION_CITY_TO = "city to";

    final String DESTINATION_CITY_FROM = "city from";

    final String TYPE = "type";

    final BigDecimal WEIGHT = BigDecimal.valueOf(55);

    final String SHIPPING_DATE = LocalDate.of(2020, 10, 10).toString();

    final String DELIVERY_DATE = LocalDate.of(2020, 4, 27).toString();;

    final BigDecimal SHIPPING_PRICE_IN_CENTS = BigDecimal.valueOf(28);

    @Mock
    ReceiptMapper receiptMapper;

    @Mock
    DestinationMapper destinationMapper;

    @Mock
    OrderTypeMapper orderTypeMapper;

    @InjectMocks
    OrderMapperImpl orderMapper;

    @Test
    void orderDtoToOrder() {

        OrderDto orderDto = OrderDto.builder().id(ID)
                .description(DESCRIPTION)
                .destinationCityFrom(DESTINATION_CITY_FROM)
                .destinationCityTo(DESTINATION_CITY_TO)
                .type(TYPE)
                .weight(WEIGHT)
                .shippingDate(SHIPPING_DATE)
                .deliveryDate(DELIVERY_DATE)
                .shippingPriceInCents(BigDecimal.valueOf(28))
                .receipt(ReceiptDto.builder().build())
                .orderType(OrderTypeDto.builder().build())
                .destination(DestinationDto.builder().build())
                .build();

        Order order = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);
        assertEquals(orderDto.getId(), order.getId());
        assertEquals(orderDto.getDescription(), order.getDescription());
        assertEquals(orderDto.getWeight(), order.getWeight());
        assertEquals(orderDto.getShippingDate(), order.getShippingDate().toString());
        assertEquals(orderDto.getDeliveryDate(), order.getDeliveryDate().toString());

    }

    @Test
    void orderToOrderDto() {

        Long checkId = 2L;
        Long typeId = 4L;
        Long destinationId = 7L;

        Order order = Order.builder()
                .id(ID)
                .description(DESCRIPTION)
                .weight(WEIGHT)
                .status(Status.PAID)
                .shippingDate(LocalDate.parse(SHIPPING_DATE))
                .deliveryDate(LocalDate.parse(DELIVERY_DATE))
                .shippingPriceInCents(BigDecimal.valueOf(28))
                .receipt(Receipt.builder().id(checkId).build())
                .orderType(OrderType.builder().id(typeId).build())
                .destination(Destination.builder().id(destinationId).build())
                .build();

        when(receiptMapper.orderCheckToOrderCheckDto(any())).thenReturn(ReceiptDto.builder().id(checkId).build());
        when(orderTypeMapper.orderTypeToOrderTypeDto(any())).thenReturn(OrderTypeDto.builder().id(typeId).build());
        when(destinationMapper.destinationToDestinationDto(any())).thenReturn(DestinationDto.builder().id(destinationId).build());
        OrderDto orderDto = orderMapper.orderToOrderDto(order);

        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getDescription(), orderDto.getDescription());
        assertEquals(order.getWeight() ,orderDto.getWeight());
        assertEquals(order.getStatus(), orderDto.getStatus());
        assertEquals(order.getShippingDate().toString(), orderDto.getShippingDate());
        assertEquals(order.getDeliveryDate().toString(), orderDto.getDeliveryDate());
        assertEquals(order.getShippingPriceInCents(), orderDto.getShippingPriceInCents());
        assertEquals(order.getReceipt().getId(), orderDto.getReceipt().getId());
        assertEquals(order.getOrderType().getId(), orderDto.getOrderType().getId());
        assertEquals(order.getDestination().getId(), orderDto.getDestination().getId());

    }
}
