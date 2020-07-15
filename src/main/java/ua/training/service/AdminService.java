package ua.training.service;

import ua.training.api.dto.StatisticsDto;
import ua.training.exception.OrderNotFoundException;

public interface AdminService {

    void shipOrder(Long orderId) throws OrderNotFoundException;

    void deliverOrder(Long orderID) throws OrderNotFoundException;

    void receiveOrder(Long orderId) throws OrderNotFoundException;

    StatisticsDto createStatisticsDto();
}
