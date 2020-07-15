package ua.training.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ua.training.api.dto.ReceiptDto;
import ua.training.domain.order.Receipt;

@Mapper(uses = UserMapper.class, componentModel = "spring")
public interface ReceiptMapper {

    ReceiptMapper INSTANCE = Mappers.getMapper(ReceiptMapper.class);

    @Mappings({
            @Mapping(target = "orderId", source = "receipt.order.id"),
            @Mapping(target = "bankCard", source = "receipt.bankCard.id"),
            @Mapping(target = "userId", source = "receipt.user.id")

    })
    ReceiptDto orderCheckToOrderCheckDto(Receipt receipt);
}
