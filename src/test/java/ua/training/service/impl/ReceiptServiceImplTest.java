package ua.training.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.OrderDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.api.dto.UserDto;
import ua.training.api.mapper.ReceiptMapper;
import ua.training.domain.order.Receipt;
import ua.training.domain.order.Status;
import ua.training.domain.user.User;
import ua.training.exception.OrderNotFoundException;
import ua.training.exception.OrderReceiptNotFoundException;
import ua.training.repository.ReceiptRepository;
import ua.training.service.OrderService;
import ua.training.service.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

    @Mock
    ReceiptRepository receiptRepository;

    @Mock
    OrderService orderService;

    @Mock
    UserService userService;

    @Mock
    ReceiptMapper receiptMapper;

    @InjectMocks
    ReceiptServiceImpl service;

    @Test
    void showAllChecks() {
        List<Receipt> receiptList = Arrays.asList(Receipt.builder().build(), Receipt.builder().build());
        when(receiptRepository.findAll()).thenReturn(receiptList);
        when(receiptMapper.orderCheckToOrderCheckDto(any(Receipt.class))).thenReturn(ReceiptDto.builder().build());

        List<ReceiptDto> result = service.showAllChecks();

        assertEquals(result.size(), receiptList.size());

        verify(receiptRepository).findAll();
        verify(receiptMapper, times(receiptList.size())).orderCheckToOrderCheckDto(any(Receipt.class));
    }

    @Test
    void showCheckById() throws OrderReceiptNotFoundException {

        final Long ID = 1L;
        Optional<Receipt> optionalOrderCheck = Optional.of(Receipt.builder().id(ID).build());

        when(receiptRepository.findById(anyLong())).thenReturn(optionalOrderCheck);
        when(receiptMapper.orderCheckToOrderCheckDto(any(Receipt.class))).thenReturn(ReceiptDto.builder().id(ID).build());

        ReceiptDto result = service.showReceiptById(ID);

        assertEquals(result.getId(), ID);
        verify(receiptRepository).findById(anyLong());
        verify(receiptMapper).orderCheckToOrderCheckDto(any(Receipt.class));
    }

    @Test
    void showCheckByIdException() throws OrderReceiptNotFoundException {

        when(receiptRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderReceiptNotFoundException.class,
                () -> {
                    service.showReceiptById(1L);
                });

        verify(receiptRepository).findById(anyLong());
    }

    @Test
    void showChecksByUser() {
        final String LOGIN1 = "login1";
        final String LOGIN2 = "login2";
        Receipt receipt1 = Receipt.builder().id(1L).user(User.builder().id(3L).login(LOGIN1).build()).build();
        Receipt receipt2 = Receipt.builder().id(2L).user(User.builder().id(4L).login(LOGIN2).build()).build();

        List<Receipt> receiptList = Arrays.asList(receipt1, receipt2);
        when(receiptRepository.findAllByUser_Login(anyString())).thenReturn(receiptList);

        List<ReceiptDto> receiptDtoList = receiptList.stream()
                .map(ReceiptMapper.INSTANCE::orderCheckToOrderCheckDto)
                .collect(Collectors.toList());

        List<ReceiptDto> result = service.showChecksByUser(LOGIN1);

        assertEquals(result.size(), receiptDtoList.size());
        assertEquals(result.get(0).getUserId(), receiptDtoList.get(0).getUserId());
        assertEquals(result.get(1).getUserId(), receiptDtoList.get(1).getUserId());

        verify(receiptRepository).findAllByUser_Login(anyString());
    }

    @Test
    void createCheckDto() throws OrderNotFoundException {
        Long orderDtoId = 1L;
        BankCardDto bankCardDto = BankCardDto.builder().id(2L).build();
        Long userId = 3L;

        when(orderService.getOrderDtoById(anyLong())).thenReturn(OrderDto.builder()
                .id(orderDtoId)
                .shippingPriceInCents(BigDecimal.ONE).build());

        when(userService.findUserDTOById(anyLong())).thenReturn(UserDto.builder().login("login").id(userId).build());

        ReceiptDto result = service.createCheckDto(orderDtoId, bankCardDto, userId);

        assertEquals(result.getOrderId(), orderDtoId);
        assertEquals(result.getBankCard(), bankCardDto.getId());
        assertEquals(result.getPriceInCents(), BigDecimal.ONE);
        assertEquals(result.getUserId(), userId);
        assertEquals(result.getStatus(), Status.NOT_PAID);

        verify(orderService).getOrderDtoById(anyLong());
        verify(userService).findUserDTOById(anyLong());
    }
}
