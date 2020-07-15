package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.training.api.dto.StatisticsDto;
import ua.training.domain.order.Order;
import ua.training.domain.order.Status;
import ua.training.exception.OrderNotFoundException;
import ua.training.repository.OrderRepository;
import ua.training.repository.ReceiptRepository;
import ua.training.service.AdminService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;

    public AdminServiceImpl(OrderRepository orderRepository, ReceiptRepository receiptRepository) {
        this.orderRepository = orderRepository;
        this.receiptRepository = receiptRepository;
    }

    @Override
    public void shipOrder(Long orderId) throws OrderNotFoundException {

        Order order = findOrderById(orderId);

        if (!order.getStatus().equals(Status.PAID)){
            throw new OrderNotFoundException();
        }

        Long daysToDeliver = order.getDestination().getDaysToDeliver();

        order.setShippingDate(LocalDate.now().plusDays(1L));
        order.setDeliveryDate(LocalDate.now().plusDays(daysToDeliver));
        order.setStatus(Status.SHIPPED);

        orderRepository.save(order);
    }

    @Override
    public void deliverOrder(Long orderId) throws OrderNotFoundException {

        Order order = findOrderById(orderId);

        if (!order.getStatus().equals(Status.SHIPPED)){
            throw new OrderNotFoundException();
        }

        order.setDeliveryDate(LocalDate.now().plusDays(1));
        order.setStatus(Status.DELIVERED);

        orderRepository.save(order);
    }

    @Override
    public void receiveOrder(Long orderId) throws OrderNotFoundException {

        Order order = findOrderById(orderId);

        if (!order.getStatus().equals(Status.DELIVERED)){
            throw new OrderNotFoundException();
        }

        order.setStatus(Status.RECEIVED);

        orderRepository.save(order);
    }

    @Override
    public StatisticsDto createStatisticsDto() {

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        BigDecimal earningsLastMonth = receiptRepository.earningsByCreationMonthsAndYear(month, year);
        BigDecimal earningsYear = receiptRepository.earningsByCreationYear(year);
        Long deliversNumber = receiptRepository.ordersByCreationMonthsAndYear(month, year);
        Long deliversNumberYear = receiptRepository.ordersByCreationYear(year);

        return StatisticsDto.builder()
                .earningsLastMonth(earningsLastMonth)
                .earningsYear(earningsYear)
                .deliversNumber(deliversNumber)
                .deliversNumberYear(deliversNumberYear)
                .numberOfOrdersByForYear(getNumberOfOrdersByForYear(year))
                .earningsOfOrdersByForYear(getEarningsOfOrdersByForYear(year))
                .build();
    }

    private Map<Integer, Long> getNumberOfOrdersByForYear(Integer year) {

        Map<Integer, Long> statistic = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            statistic.put(i, receiptRepository.ordersByCreationMonthsAndYear(i, year));
        }

        return statistic;
    }

    private Map<Integer, BigDecimal> getEarningsOfOrdersByForYear(Integer year) {

        Map<Integer, BigDecimal> statistic = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            statistic.put(i, receiptRepository.earningsByCreationMonthsAndYear(i, year));
        }

        return statistic;
    }

    private Order findOrderById(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("no order with id=" + orderId));
    }

}
