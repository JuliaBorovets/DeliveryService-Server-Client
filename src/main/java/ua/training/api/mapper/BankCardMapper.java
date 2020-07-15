package ua.training.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ua.training.api.dto.BankCardDto;
import ua.training.domain.user.BankCard;

@Mapper(componentModel = "spring")
public interface BankCardMapper {

    BankCardMapper INSTANCE = Mappers.getMapper(BankCardMapper.class);

    BankCardDto bankCardToDto(BankCard bankCard);

    BankCard bankCardDtoToBankCard(BankCardDto bankCardDto);
}
