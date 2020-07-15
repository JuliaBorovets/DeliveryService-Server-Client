package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.training.api.dto.OrderTypeDto;
import ua.training.api.mapper.OrderTypeMapper;
import ua.training.domain.order.OrderType;
import ua.training.exception.OrderTypeNotFoundException;
import ua.training.repository.OrderTypeRepository;
import ua.training.service.OrderTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderTypeServiceImpl implements OrderTypeService {

    private final OrderTypeRepository orderTypeRepository;
    private final OrderTypeMapper orderTypeMapper;

    public OrderTypeServiceImpl(OrderTypeRepository orderTypeRepository, OrderTypeMapper orderTypeMapper) {
        this.orderTypeRepository = orderTypeRepository;
        this.orderTypeMapper = orderTypeMapper;
    }

    @Override
    public List<OrderTypeDto> getAllOrderTypeDto() {
        return orderTypeRepository.findAll().stream()
                .map(orderTypeMapper::orderTypeToOrderTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderType getOrderTypeById(Long id) throws OrderTypeNotFoundException {
        return orderTypeRepository.findById(id)
                .orElseThrow(() -> new OrderTypeNotFoundException("no order type with id=" + id));
    }
}
