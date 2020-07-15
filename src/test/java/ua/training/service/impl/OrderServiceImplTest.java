package ua.training.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import ua.training.api.dto.OrderDto;
import ua.training.api.dto.OrderTypeDto;
import ua.training.api.mapper.OrderMapper;
import ua.training.domain.order.Destination;
import ua.training.domain.order.Order;
import ua.training.domain.order.OrderType;
import ua.training.domain.order.Status;
import ua.training.domain.user.User;
import ua.training.exception.*;
import ua.training.repository.OrderRepository;
import ua.training.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource
@ContextConfiguration(classes = OrderServiceImpl.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    OrderTypeServiceImpl orderTypeService;

    @Mock
    DestinationServiceImpl destinationService;

    @Spy
    private List<Order> orderList = new ArrayList<>();

    @InjectMocks
    OrderServiceImpl service;

    final BigDecimal BASE_PRICE = BigDecimal.valueOf(5);
    final BigDecimal WEIGHT_COEFFICIENT = BigDecimal.valueOf(0.25);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "BASE_PRICE", BASE_PRICE);
        ReflectionTestUtils.setField(service, "WEIGHT_COEFFICIENT", WEIGHT_COEFFICIENT);
    }

    @Test
    void findAllUserOrders() {

        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.NOT_PAID).build(),
                Order.builder().status(Status.NOT_PAID).build()
        );

        when(orderRepository.findOrderByOwner_Login(anyString()))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.NOT_PAID).build());

        List<OrderDto> result = service.findAllUserOrders("login");

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findOrderByOwner_Login(anyString());
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void findAllNotPaidUserOrders() {
        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.NOT_PAID).build(),
                Order.builder().status(Status.NOT_PAID).build()
        );

        when(orderRepository.findByStatusAndOwner_Login(any(Status.class), anyString()))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.NOT_PAID).build());

        List<OrderDto> result = service.findAllNotPaidUserOrders("login");

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findByStatusAndOwner_Login(any(Status.class), anyString());
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void findAllArchivedUserOrders() {
        final Long ID = 1L;
        User user = User.builder().id(ID).build();

        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.ARCHIVED).owner(user).build(),
                Order.builder().status(Status.ARCHIVED).owner(user).build()
        );

        when(orderRepository.findByStatusAndOwner_Login(any(Status.class), anyString()))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.ARCHIVED).build());

        List<OrderDto> result = service.findAllArchivedUserOrders("login");

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findByStatusAndOwner_Login(any(Status.class), anyString());
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void findAllDeliveredUserOrders() {
        final Long ID = 1L;
        User user = User.builder().id(ID).build();

        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.DELIVERED).owner(user).build(),
                Order.builder().status(Status.DELIVERED).owner(user).build()
        );

        when(orderRepository.findByStatusAndOwner_Login(any(Status.class), anyString()))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.DELIVERED).build());

        List<OrderDto> result = service.findAllDeliveredUserOrders("login");

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findByStatusAndOwner_Login(any(Status.class), anyString());
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void findAllPaidOrdersDTO() {

        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.PAID).build(),
                Order.builder().status(Status.PAID).build()
        );

        when(orderRepository.findOrderByStatus(any(Status.class)))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.PAID).build());

        List<OrderDto> result = service.findAllPaidOrdersDTO();

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findOrderByStatus(any(Status.class));
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void findAllShippedOrdersDTO() {
        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.SHIPPED).build(),
                Order.builder().status(Status.SHIPPED).build()
        );

        when(orderRepository.findOrderByStatus(any(Status.class)))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.SHIPPED).build());

        List<OrderDto> result = service.findAllShippedOrdersDTO();

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findOrderByStatus(any(Status.class));
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void findAllDeliveredOrdersDto() {
        List<Order> orderList = Arrays.asList(
                Order.builder().status(Status.DELIVERED).build(),
                Order.builder().status(Status.DELIVERED).build()
        );

        when(orderRepository.findOrderByStatus(any(Status.class)))
                .thenReturn(orderList);

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(OrderDto.builder().status(Status.DELIVERED).build());

        List<OrderDto> result = service.findAllDeliveredOrdersDto();

        assertEquals(result.size(), orderList.size());
        verify(orderRepository).findOrderByStatus(any(Status.class));
        verify(orderMapper, times(orderList.size())).orderToOrderDto(any(Order.class));
    }

    @Test
    void getOrderDtoById() throws OrderNotFoundException {

        final Long ID = 1L;

        Order order = Order.builder().id(ID).build();
        OrderDto orderDto = OrderDto.builder().id(ID).build();

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(orderDto);

        OrderDto result = service.getOrderDtoById(ID);
        assertEquals(ID, result.getId());
        verify(orderRepository).findById(anyLong());
        verify(orderMapper).orderToOrderDto(any(Order.class));
    }

    @Test
    void getOrderDtoByIdAndUserId() throws OrderNotFoundException {

        final Long ID = 1L;
        final Long USER_ID = 2L;
        final String LOGIN = "login";

        User user = User.builder().id(USER_ID).login(LOGIN).build();

        Order order = Order.builder().id(ID).owner(user).build();
        OrderDto orderDto = OrderDto.builder().id(ID).build();

        when(orderRepository.findByIdAndOwner_Login(anyLong(), anyString()))
                .thenReturn(Optional.of(order));

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(orderDto);

        OrderDto result = service.getOrderDtoByIdAndUserId(ID, LOGIN);
        assertEquals(ID, result.getId());
        verify(orderRepository).findByIdAndOwner_Login(anyLong(), anyString());
        verify(orderMapper).orderToOrderDto(any(Order.class));
    }

    @Test
    void findOrderById() throws OrderNotFoundException {

        final Long ID = 1L;

        Order order = Order.builder().id(ID).build();

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        Order result = service.findOrderById(ID);

        assertEquals(ID, result.getId());
        verify(orderRepository).findById(anyLong());
    }

    @Test
    void moveOrderToArchive() throws OrderNotFoundException {
        final Long ID = 1L;

        Order order = Order.builder().id(ID).status(Status.RECEIVED).build();
        OrderDto orderDto = OrderDto.builder().id(order.getId()).status(Status.ARCHIVED).build();

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(orderMapper.orderToOrderDto(any(Order.class)))
                .thenReturn(orderDto);

        OrderDto result = service.moveOrderToArchive(ID);

        assertEquals(ID, result.getId());
        verify(orderRepository).findById(anyLong());
        verify(orderRepository).save(order);
        assertEquals(Status.ARCHIVED, result.getStatus());

    }

    @Test
    void deleteOrderById() throws OrderNotFoundException {

        final Long ID = 1L;

        Order order = Order.builder().id(ID).status(Status.NOT_PAID).build();

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        service.deleteOrderById(ID);
        verify(orderRepository).delete(any(Order.class));
    }

    @Test
    void deleteOrderByIdPaid() throws OrderNotFoundException {

        final Long ID = 1L;

        Order order = Order.builder().id(ID).status(Status.PAID).build();

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        service.deleteOrderById(ID);
        verify(orderRepository).findById(anyLong());
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void orderFindException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> {
                    service.findOrderById(1L);
                });

        verify(orderRepository).findById(anyLong());
    }

    @Test
    void calculatePrice() {

        BigDecimal result = service.calculatePrice(Order.builder()
                .destination(Destination.builder().priceInCents(BigDecimal.ONE).build())
                .weight(BigDecimal.ONE)
                .orderType(OrderType.builder().priceInCents(BigDecimal.ONE).build())
                .build());

        assertNotNull(result);
    }

    @Test
    void createOrderException() {
        Order order = Order.builder().build();
        when(orderMapper.orderDtoToOrder(any(OrderDto.class))).thenReturn(order);

        when(userRepository.findByLogin(anyString())).thenThrow(new UserNotFoundException("no user"));

        assertThrows(UserNotFoundException.class,
                () -> {
                    service.createOrder(OrderDto.builder().build(), "login");
                });
    }

    @Test
    void createOrder() throws OrderTypeNotFoundException, DestinationNotFoundException {
        Order order = Order.builder()
                .weight(BigDecimal.ONE)
                .destination(Destination.builder().priceInCents(BigDecimal.ONE).build())
                .orderType(OrderType.builder().priceInCents(BigDecimal.ONE).build()).build();
        OrderDto orderDto = OrderDto.builder()
                .orderType(OrderTypeDto.builder().id(1L).build())
                .type("1")
                .destinationCityFrom("from")
                .destinationCityTo("to").build();

        when(orderMapper.orderDtoToOrder(any(OrderDto.class))).thenReturn(order);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(User.builder().orders(orderList).build()));

        when(orderTypeService.getOrderTypeById(anyLong())).thenReturn(
                OrderType.builder().priceInCents(BigDecimal.ONE).build());
        when(destinationService.getDestination(anyString(), anyString())).thenReturn(
                Destination.builder().priceInCents(BigDecimal.ONE).build());
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.orderToOrderDto(any(Order.class))).thenReturn(orderDto);

        service.createOrder(orderDto, "login");

        verify(orderMapper).orderDtoToOrder(any(OrderDto.class));
        verify(userRepository).findByLogin(anyString());
        verify(orderTypeService).getOrderTypeById(anyLong());
        verify(destinationService).getDestination(anyString(), anyString());
        verify(orderRepository).save(any());
        verify(orderMapper).orderToOrderDto(any(Order.class));
    }

    @Test
    void createOrderExc() throws OrderTypeNotFoundException, DestinationNotFoundException {
        Order order = Order.builder()
                .weight(BigDecimal.ONE)
                .destination(Destination.builder().priceInCents(BigDecimal.ONE).build())
                .orderType(OrderType.builder().priceInCents(BigDecimal.ONE).build()).build();
        OrderDto orderDto = OrderDto.builder()
                .orderType(OrderTypeDto.builder().id(1L).build())
                .type("1")
                .destinationCityFrom("from")
                .destinationCityTo("to").build();

        when(orderMapper.orderDtoToOrder(any(OrderDto.class))).thenReturn(order);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(User.builder().orders(orderList).build()));

        when(orderTypeService.getOrderTypeById(anyLong())).thenThrow(new OrderTypeNotFoundException("exc"));

        assertThrows(OrderCreateException.class,
                () -> {
                    service.createOrder(orderDto, "login");
                });
    }
}
