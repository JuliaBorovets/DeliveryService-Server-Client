package ua.training.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.training.api.dto.StatisticsDto;
import ua.training.domain.order.Destination;
import ua.training.domain.order.Order;
import ua.training.domain.order.Status;
import ua.training.exception.OrderNotFoundException;
import ua.training.repository.OrderRepository;
import ua.training.repository.ReceiptRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ReceiptRepository receiptRepository;

    @InjectMocks
    AdminServiceImpl service;

    @Test
    void shipOrder() throws OrderNotFoundException {

        final Long ID = 2L;

        Optional<Order> order = Optional.ofNullable(Order.builder()
                .id(ID)
                .status(Status.PAID)
                .destination(Destination.builder().daysToDeliver(3L).build()).build());

        when(orderRepository.findById(anyLong())).thenReturn(order);

        service.shipOrder(ID);

        assertNotNull(order.get().getShippingDate());
        assertNotNull(order.get().getDeliveryDate());
        assertEquals(order.get().getStatus(), Status.SHIPPED);

    }

    @Test
    void shipOrderException() throws OrderNotFoundException {

        final Long ID = 2L;

        Optional<Order> order = Optional.ofNullable(Order.builder()
                .id(ID)
                .status(Status.NOT_PAID)
                .destination(Destination.builder().daysToDeliver(3L).build()).build());

        when(orderRepository.findById(anyLong())).thenReturn(order);

        assertThrows(OrderNotFoundException.class,
                () -> {
                    service.shipOrder(ID);
                });

        assertNull(order.get().getShippingDate());
        assertNull(order.get().getDeliveryDate());
        assertEquals(order.get().getStatus(), Status.NOT_PAID);

    }

    @Test
    void deliverOrder() throws OrderNotFoundException {

        final Long ID = 2L;

        Optional<Order> order = Optional.ofNullable(Order.builder()
                .id(ID)
                .status(Status.SHIPPED)
                .destination(Destination.builder().daysToDeliver(3L).build()).build());

        when(orderRepository.findById(anyLong())).thenReturn(order);

        service.deliverOrder(ID);

        assertEquals(order.get().getStatus(), Status.DELIVERED);
    }

    @Test
    void deliverOrderException() throws OrderNotFoundException {

        final Long ID = 2L;

        Optional<Order> order = Optional.ofNullable(Order.builder()
                .id(ID)
                .status(Status.NOT_PAID)
                .destination(Destination.builder().daysToDeliver(3L).build()).build());

        when(orderRepository.findById(anyLong())).thenReturn(order);

        assertThrows(OrderNotFoundException.class,
                () -> {
                    service.deliverOrder(ID);
                });

        assertEquals(order.get().getStatus(), Status.NOT_PAID);
    }

    @Test
    void receiveOrder() throws OrderNotFoundException {

        final Long ID = 2L;

        Optional<Order> order = Optional.ofNullable(Order.builder()
                .id(ID)
                .status(Status.DELIVERED)
                .destination(Destination.builder().daysToDeliver(3L).build()).build());

        when(orderRepository.findById(anyLong())).thenReturn(order);

        service.receiveOrder(ID);

        assertEquals(order.get().getStatus(), Status.RECEIVED);
    }

    @Test
    void receiveOrderException() throws OrderNotFoundException {

        final Long ID = 2L;

        Optional<Order> order = Optional.ofNullable(Order.builder()
                .id(ID)
                .status(Status.NOT_PAID)
                .destination(Destination.builder().daysToDeliver(3L).build()).build());

        when(orderRepository.findById(anyLong())).thenReturn(order);

        assertThrows(OrderNotFoundException.class,
                () -> {
                    service.receiveOrder(ID);
                });

        assertEquals(order.get().getStatus(), Status.NOT_PAID);
    }

    @Test
    void createStatisticsDto() {

        final Long ORDER_NUMBER = 2L;
        final BigDecimal EARNINGS = BigDecimal.valueOf(45);

        BigDecimal earningsLastMonth = BigDecimal.valueOf(12);
        BigDecimal earningsYear = BigDecimal.valueOf(34);
        Long deliversNumber = 14L;
        Long deliversNumberYear = 5L;

        when(receiptRepository.earningsByCreationMonthsAndYear(anyInt(), anyInt())).thenReturn(earningsLastMonth);
        when(receiptRepository.earningsByCreationYear(anyInt())).thenReturn(earningsYear);
        when(receiptRepository.ordersByCreationYear(anyInt())).thenReturn(deliversNumberYear);
        when(receiptRepository.ordersByCreationMonthsAndYear(anyInt(), anyInt())).thenReturn(deliversNumber);

        StatisticsDto result = service.createStatisticsDto();

        assertEquals(earningsLastMonth, result.getEarningsLastMonth());
        assertEquals(earningsYear, result.getEarningsYear());
        assertEquals(deliversNumber, result.getDeliversNumber());
        assertEquals(deliversNumberYear, result.getDeliversNumberYear());
        assertNotNull(result.getEarningsOfOrdersByForYear());
        assertNotNull(result.getNumberOfOrdersByForYear());

        //12 month + 1
        verify(receiptRepository, times(13)).earningsByCreationMonthsAndYear(anyInt(), anyInt());
        verify(receiptRepository, times(13)).ordersByCreationMonthsAndYear(anyInt(), anyInt());

        verify(receiptRepository).earningsByCreationYear(anyInt());
        verify(receiptRepository).ordersByCreationYear(anyInt());
    }
}
