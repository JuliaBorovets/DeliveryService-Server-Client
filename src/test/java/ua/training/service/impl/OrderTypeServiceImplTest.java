package ua.training.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.training.api.dto.OrderTypeDto;
import ua.training.api.mapper.OrderTypeMapper;
import ua.training.domain.order.OrderType;
import ua.training.exception.OrderTypeNotFoundException;
import ua.training.repository.OrderTypeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTypeServiceImplTest {

    @Mock
    OrderTypeRepository orderTypeRepository;

    @Mock
    OrderTypeMapper orderTypeMapper;

    @InjectMocks
    OrderTypeServiceImpl service;


    @Test
    void getAllOrderTypeDto() {
        List<OrderType> orderTypeList = Arrays.asList(OrderType.builder().build(), OrderType.builder().build());

        when(orderTypeRepository.findAll())
                .thenReturn(orderTypeList);

        when(orderTypeMapper.orderTypeToOrderTypeDto(any(OrderType.class)))
                .thenReturn(OrderTypeDto.builder().build());

        List<OrderTypeDto> result = service.getAllOrderTypeDto();

        assertEquals(result.size(), orderTypeList.size());
        verify(orderTypeRepository).findAll();
        verify(orderTypeMapper, times(orderTypeList.size())).orderTypeToOrderTypeDto(any(OrderType.class));
    }

    @Test
    void getOrderTypeById() throws OrderTypeNotFoundException {

        final Long ID = 1L;

        OrderType orderType = OrderType.builder().id(ID).build();

        when(orderTypeRepository.findById(anyLong()))
                .thenReturn(Optional.of(orderType));

        OrderType result = service.getOrderTypeById(ID);

        assertEquals(result.getId(), ID);
        verify(orderTypeRepository).findById(anyLong());
    }

    @Test
    void getOrderTypeByIdException() {
        when(orderTypeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderTypeNotFoundException.class,
                () -> {
                    service.getOrderTypeById(1L);
                });
    }
}
