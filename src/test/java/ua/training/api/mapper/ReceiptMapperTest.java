package ua.training.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.training.api.dto.ReceiptDto;
import ua.training.domain.order.Order;
import ua.training.domain.order.Receipt;
import ua.training.domain.user.BankCard;
import ua.training.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
class ReceiptMapperTest {

    final Long ID = 2L;

    final Long ORDER_ID = 3L;

    final BigDecimal PRICE_IN_CENTS = BigDecimal.valueOf(67);

    final Long BANK_CARD = 7L;

    @Test
    void orderCheckToOrderCheckDto() {

        Receipt receipt = new Receipt();
        receipt.setId(ID);
        receipt.setOrder(Order.builder().id(ORDER_ID).build());
        receipt.setPriceInCents(PRICE_IN_CENTS);
        receipt.setBankCard(BankCard.builder().id(BANK_CARD).build());
        receipt.setCreationDate(LocalDate.now());
        receipt.setUser(User.builder().id(7L).build());

        ReceiptDto receiptDto = ReceiptMapper.INSTANCE.orderCheckToOrderCheckDto(receipt);
        assertEquals(receiptDto.getId(), receipt.getId());
        assertEquals(receiptDto.getOrderId(), receipt.getOrder().getId());
        assertEquals(receiptDto.getPriceInCents(), receipt.getPriceInCents());
        assertEquals(receiptDto.getBankCard(), receipt.getBankCard().getId());
        assertEquals(receiptDto.getCreationDate(), receipt.getCreationDate());
        assertEquals(receiptDto.getUserId(), receipt.getUser().getId());
    }
}
