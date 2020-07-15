package ua.training.service;

import ua.training.api.dto.OrderTypeDto;
import ua.training.domain.order.OrderType;
import ua.training.exception.OrderTypeNotFoundException;

import java.util.List;

public interface OrderTypeService {

    List<OrderTypeDto> getAllOrderTypeDto();

    OrderType getOrderTypeById(Long id) throws OrderTypeNotFoundException;
}
