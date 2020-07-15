package ua.training.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ua.training.api.dto.OrderTypeDto;
import ua.training.domain.order.OrderType;

@Mapper(componentModel = "spring")
public interface OrderTypeMapper {

    OrderTypeMapper INSTANCE = Mappers.getMapper(OrderTypeMapper.class);

    OrderTypeDto orderTypeToOrderTypeDto(OrderType orderType);
}
