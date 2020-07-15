package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.training.api.dto.OrderDto;
import ua.training.api.mapper.OrderMapper;
import ua.training.domain.order.Order;
import ua.training.domain.order.Status;
import ua.training.domain.user.User;
import ua.training.exception.*;
import ua.training.repository.OrderRepository;
import ua.training.repository.UserRepository;
import ua.training.service.DestinationService;
import ua.training.service.OrderService;
import ua.training.service.OrderTypeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@PropertySource("classpath:constants.properties")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final OrderTypeService orderTypeService;
    private final DestinationService destinationService;

    @Value("${constants.BASE.PRICE}")
    private BigDecimal BASE_PRICE;

    @Value("${constants.WEIGHT.COEFFICIENT}")
    private BigDecimal WEIGHT_COEFFICIENT;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderMapper orderMapper,
                            OrderTypeService orderTypeService, DestinationService destinationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.orderTypeService = orderTypeService;
        this.destinationService = destinationService;
    }

    public List<OrderDto> findAllUserOrders(String login) {
        return orderRepository.findOrderByOwner_Login(login).stream()
                        .map(orderMapper::orderToOrderDto)
                        .filter(o -> !o.getStatus().equals(Status.ARCHIVED))
                        .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findAllNotPaidUserOrders(String login) {
        return orderRepository.findByStatusAndOwner_Login(Status.NOT_PAID, login).stream()
                        .map(orderMapper::orderToOrderDto)
                        .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findAllArchivedUserOrders(String login) {
        return orderRepository.findByStatusAndOwner_Login(Status.ARCHIVED, login).stream()
                        .map(orderMapper::orderToOrderDto)
                        .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findAllDeliveredUserOrders(String login) {
        return orderRepository.findByStatusAndOwner_Login(Status.DELIVERED, login).stream()
                        .map(orderMapper::orderToOrderDto)
                        .collect(Collectors.toList());
    }

    public List<OrderDto> findAllPaidOrdersDTO() {
        return orderRepository.findOrderByStatus(Status.PAID).stream()
                        .map(orderMapper::orderToOrderDto)
                        .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findAllShippedOrdersDTO() {
        return orderRepository.findOrderByStatus(Status.SHIPPED).stream()
                        .map(orderMapper::orderToOrderDto)
                        .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findAllDeliveredOrdersDto() {
        return orderRepository.findOrderByStatus(Status.DELIVERED).stream()
                        .map(orderMapper::orderToOrderDto)
                        .collect(Collectors.toList());
    }

    public BigDecimal calculatePrice(Order order) {

        BigDecimal priceForDestination = order.getDestination().getPriceInCents();

        BigDecimal priceForWeight = order.getWeight().multiply(WEIGHT_COEFFICIENT);

        BigDecimal priceForType = order.getOrderType().getPriceInCents();

        return priceForDestination.add(priceForType).add(priceForWeight).add(BASE_PRICE);

    }

    @Override
    public OrderDto getOrderDtoById(Long id) throws OrderNotFoundException {
        Order order = findOrderById(id);

        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto getOrderDtoByIdAndUserId(Long id, String login) throws OrderNotFoundException {

        Order order = orderRepository.findByIdAndOwner_Login(id, login)
                .orElseThrow(() -> new OrderNotFoundException("no order with id=" + id));

        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("no order with id=" + orderId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = OrderCreateException.class)
    @Override
    public OrderDto createOrder(OrderDto orderDTO, String login) throws OrderCreateException, UserNotFoundException {
        Order orderToSave = orderMapper.orderDtoToOrder(orderDTO);

        User userToSave = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("no user with login=" + login));

        try {

            orderToSave.setOrderType(orderTypeService.getOrderTypeById(Long.valueOf(orderDTO.getType())));
            orderToSave.setDestination(destinationService.getDestination(orderDTO.getDestinationCityFrom(),
                    orderDTO.getDestinationCityTo()));
            orderToSave.saveOrder(userToSave);
            orderToSave.setShippingDate(LocalDate.now());
            orderToSave.setShippingPriceInCents(calculatePrice(orderToSave));
            Order order = orderRepository.save(orderToSave);
            return orderMapper.orderToOrderDto(order);
        } catch (DataIntegrityViolationException | OrderTypeNotFoundException | DestinationNotFoundException e) {
            throw new OrderCreateException("Can not create order with id = " + orderDTO.getId());
        }
    }

    @Override
    public OrderDto moveOrderToArchive(Long orderId) throws OrderNotFoundException {
        Order order = findOrderById(orderId);

        if (order.getStatus().equals(Status.RECEIVED)) {
            order.setStatus(Status.ARCHIVED);
            orderRepository.save(order);
        }
        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public void deleteOrderById(Long orderId) throws OrderNotFoundException {
        Order order = findOrderById(orderId);

        if (order.getStatus().equals(Status.NOT_PAID)){
            orderRepository.delete(order);
        }

        log.info("deleting order");
    }
}
