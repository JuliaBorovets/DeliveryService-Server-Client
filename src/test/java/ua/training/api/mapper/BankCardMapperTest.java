package ua.training.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.training.api.dto.BankCardDto;
import ua.training.domain.user.BankCard;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class BankCardMapperTest {

    final Long ID = 1L;

    final Long EXP_MONTH = 11L;

    final Long EXP_YEAR = 2023L;

    final Long CCV = 234L;

    final BigDecimal BALANCE = BigDecimal.valueOf(30);

    @Test
    void bankCardToDto() {
        BankCard bankCard = new BankCard();
        bankCard.setId(ID);
        bankCard.setExpMonth(EXP_MONTH);
        bankCard.setExpYear(EXP_YEAR);
        bankCard.setCcv(CCV);

        BankCardDto bankCardDto = BankCardMapper.INSTANCE.bankCardToDto(bankCard);

        assertEquals(bankCardDto.getId(), ID);
        assertEquals(bankCardDto.getExpMonth(), EXP_MONTH);
        assertEquals(bankCardDto.getExpYear(), EXP_YEAR);
        assertEquals(bankCardDto.getCcv(), CCV);
        assertEquals(bankCard.getBalance(), BigDecimal.ZERO);
    }

    @Test
    void bankCardDtoToBankCard() {

        BankCardDto bankCardDto = new BankCardDto();
        bankCardDto.setId(ID);
        bankCardDto.setExpMonth(EXP_MONTH);
        bankCardDto.setExpYear(EXP_YEAR);
        bankCardDto.setCcv(CCV);
        bankCardDto.setBalance(BALANCE);

        BankCard bankCard = BankCardMapper.INSTANCE.bankCardDtoToBankCard(bankCardDto);

        assertEquals(bankCard.getId(), ID);
        assertEquals(bankCard.getExpMonth(), EXP_MONTH);
        assertEquals(bankCard.getExpYear(), EXP_YEAR);
        assertEquals(bankCard.getCcv(), CCV);
        assertEquals(bankCard.getBalance(), BALANCE);
    }
}
