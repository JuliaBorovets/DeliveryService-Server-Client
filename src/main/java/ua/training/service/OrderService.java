package ua.training.service;

import ua.training.api.dto.OrderDto;
import ua.training.domain.order.Order;
import ua.training.exception.OrderCreateException;
import ua.training.exception.OrderNotFoundException;
import ua.training.exception.UserNotFoundException;

import java.util.List;

public interface OrderService {

    List<OrderDto> findAllUserOrders(String login);

    List<OrderDto> findAllNotPaidUserOrders(String login);

    List<OrderDto> findAllArchivedUserOrders(String login);

    List<OrderDto> findAllDeliveredUserOrders(String login);

    OrderDto createOrder(OrderDto orderDTO, String login) throws OrderCreateException, UserNotFoundException;

    OrderDto getOrderDtoById(Long id) throws OrderNotFoundException;

    OrderDto getOrderDtoByIdAndUserId(Long id, String login) throws OrderNotFoundException;

    List<OrderDto> findAllPaidOrdersDTO();

    List<OrderDto> findAllShippedOrdersDTO();

    List<OrderDto> findAllDeliveredOrdersDto();

    Order findOrderById(Long orderId) throws OrderNotFoundException;

    OrderDto moveOrderToArchive(Long orderId) throws OrderNotFoundException;

    void deleteOrderById(Long orderId) throws OrderNotFoundException;

}
