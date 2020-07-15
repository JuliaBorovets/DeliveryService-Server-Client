package ua.training.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.api.mapper.BankCardMapper;
import ua.training.domain.order.Order;
import ua.training.domain.order.Status;
import ua.training.domain.user.BankCard;
import ua.training.domain.user.User;
import ua.training.exception.BankCardException;
import ua.training.exception.OrderNotFoundException;
import ua.training.repository.BankCardRepository;
import ua.training.repository.ReceiptRepository;
import ua.training.service.OrderService;
import ua.training.service.UserService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankCardServiceImplTest {

    @Mock
    BankCardRepository bankCardRepository;

    @Mock
    UserService userService;

    @Mock
    OrderService orderService;

    @Mock
    ReceiptRepository receiptRepository;

    @Mock
    BankCardMapper bankCardMapper;

    @InjectMocks
    BankCardServiceImpl service;

    @Spy
    private List<User> userList = new ArrayList<>();

    @Spy
    private Set<BankCard> cards = new HashSet<>();

    @Test
    void deleteBankCardConnectionWithUser() throws BankCardException {

        BankCard bankCard = BankCard.builder().id(3L).users(userList).build();
        User user = User.builder().id(5L).cards(cards).build();
        user.getCards().add(bankCard);
        bankCard.getUsers().add(user);

        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankCard));

        when(userService.findByLogin(anyString())).thenReturn(user);
        when( bankCardMapper.bankCardToDto(any())).thenReturn(BankCardDto.builder().id(3L).build());

        BankCardDto result = service.deleteBankCardConnectionWithUser(1L, "login");

        verify(bankCardRepository).findById(anyLong());
        verify(bankCardMapper).bankCardToDto(any());
        verify(userService).findByLogin(anyString());
    }

    @Test
    void getAllUserBankCards() {
        List<BankCard> bankCardList = Arrays.asList(
                BankCard.builder().build(),
                BankCard.builder().build()
        );
        User user = User.builder().id(5L).cards(cards).build();
        when(bankCardRepository.findBankCardByUsers(any())).thenReturn(bankCardList);
        when(userService.findByLogin(anyString())).thenReturn(user);
        when( bankCardMapper.bankCardToDto(any())).thenReturn(BankCardDto.builder().id(3L).build());

        List<BankCardDto> result = service.getAllUserBankCards("login");

        verify(bankCardRepository).findBankCardByUsers(any());
        verify(userService).findByLogin(anyString());
        verify(bankCardMapper, times(bankCardList.size())).bankCardToDto(any());

        assertEquals(bankCardList.size(), result.size());
    }

    @Test
    void saveBankCardDTO() throws BankCardException {
        final Long ID = 1L;
        User user = User.builder().id(5L).cards(cards).build();
        BankCard bankCard = BankCard.builder().id(ID).ccv(123L).expMonth(12L).expYear(2020L).build();
        BankCardDto bankCardDto = BankCardDto.builder().id(ID).ccv(123L).expMonth(12L).expYear(2020L).build();

        when(userService.findByLogin(anyString())).thenReturn(user);
        when(bankCardRepository.findBankCardByIdAndExpMonthAndExpYearAndCcv(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.of(bankCard));
//        when(bankCardMapper.bankCardDtoToBankCard(any()))
//                .thenReturn(bankCard);
        when(bankCardMapper.bankCardToDto(any()))
                .thenReturn(bankCardDto);

        BankCardDto result = service.saveBankCardDTO(bankCardDto, "login");

        verify(userService).findByLogin(anyString());
        verify(bankCardRepository).findBankCardByIdAndExpMonthAndExpYearAndCcv(anyLong(), anyLong(), anyLong(), anyLong());
        verify(bankCardMapper).bankCardToDto(any());
//        verify(bankCardMapper).bankCardDtoToBankCard(any());
        verify(bankCardRepository).save(any());
    }

    @Test
    void saveBankCardDTOExists() throws BankCardException {
        final Long ID = 1L;
        User user = User.builder().id(5L).cards(cards).build();
        BankCard bankCard = BankCard.builder().id(ID).ccv(123L).expMonth(12L).expYear(2020L).build();
        BankCardDto bankCardDto = BankCardDto.builder().id(ID).ccv(123L).expMonth(12L).expYear(2020L).build();

        when(userService.findByLogin(anyString())).thenReturn(user);
        when(bankCardRepository.findBankCardByIdAndExpMonthAndExpYearAndCcv(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(bankCardMapper.bankCardDtoToBankCard(any()))
                .thenReturn(bankCard);
        when(bankCardMapper.bankCardToDto(any()))
                .thenReturn(bankCardDto);

        BankCardDto result = service.saveBankCardDTO(bankCardDto, "login");

        verify(userService).findByLogin(anyString());
        verify(bankCardRepository).findBankCardByIdAndExpMonthAndExpYearAndCcv(anyLong(), anyLong(), anyLong(), anyLong());
        verify(bankCardMapper).bankCardToDto(any());
        verify(bankCardMapper).bankCardDtoToBankCard(any());
        verify(bankCardRepository).save(any());
    }


    @Test
    void updateBankCardDTO() throws BankCardException {
        final Long ID = 2L;
        final BigDecimal BALANCE = BigDecimal.valueOf(33);

        BankCardDto bankCardDto = BankCardDto.builder()
                .id(ID)
                .balance(BALANCE).build();

        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(BankCard.builder().build()));

        when(bankCardMapper.bankCardToDto(any())).thenReturn(bankCardDto);
        when(bankCardRepository.save(any())).thenReturn(BankCard.builder().build());

        BankCardDto result = service.updateBankCardDTO(bankCardDto);

        verify(bankCardRepository).save(any(BankCard.class));
        verify(bankCardMapper).bankCardToDto(any(BankCard.class));
        verify(bankCardRepository).findById(anyLong());
        assertEquals(BALANCE, result.getBalance());
    }

    @Test
    void payForOrderBankCardException() throws OrderNotFoundException {
        final Long ID = 4L;
        User user = User.builder().id(5L).cards(cards).build();
        Order order = Order.builder()
                .id(4L)
                .status(Status.SHIPPED).build();
        ReceiptDto receiptDto = ReceiptDto.builder()
                .bankCard(3L)
                .orderId(ID).build();

        when(userService.findByLogin(anyString()))
                .thenReturn(user);
        when(orderService.findOrderById(anyLong()))
                .thenReturn(order);
        assertThrows(BankCardException.class,
                () -> {
                    service.payForOrder(receiptDto, "login");
                });

        verify(userService).findByLogin(anyString());
        verify(orderService).findOrderById(anyLong());
    }

    @Test
    void payForOrderCanNotPayException() throws OrderNotFoundException {
        final Long ID = 4L;
        User user = User.builder().id(5L).cards(cards).build();

        Order order = Order.builder()
                .id(4L)
                .shippingPriceInCents(BigDecimal.TEN)
                .status(Status.NOT_PAID).build();

        ReceiptDto receiptDto = ReceiptDto.builder()
                .bankCard(3L)
                .orderId(ID).build();

        BankCard bankCard = BankCard.builder()
                .balance(BigDecimal.ONE)
                .build();

        when(userService.findByLogin(anyString()))
                .thenReturn(user);
        when(orderService.findOrderById(anyLong()))
                .thenReturn(order);
        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankCard));

        assertThrows(BankCardException.class,
                () -> {
                    service.payForOrder(receiptDto, "login");
                });
        verify(userService).findByLogin(anyString());
        verify(orderService).findOrderById(anyLong());
        verify(bankCardRepository).findById(anyLong());

    }

    @Test
    void payForOrder() throws OrderNotFoundException, BankCardException {
        final Long ID = 4L;
        User user = User.builder().id(5L).cards(cards).build();

        Order order = Order.builder()
                .id(4L)
                .shippingPriceInCents(BigDecimal.valueOf(5L))
                .status(Status.NOT_PAID).build();

        ReceiptDto receiptDto = ReceiptDto.builder()
                .bankCard(3L)
                .orderId(ID).build();

        BankCard bankCard = BankCard.builder()
                .id(6L)
                .balance(BigDecimal.valueOf(55L))
                .build();

        BankCard bankCardToSend = BankCard.builder()
                .id(123L)
                .ccv(345L)
                .expMonth(3L)
                .expYear(2020L).build();

        when(userService.findByLogin(anyString()))
                .thenReturn(user);
        when(orderService.findOrderById(anyLong()))
                .thenReturn(order);
        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.findBankCardByIdAndExpMonthAndExpYearAndCcv(any(), any(), any(), any()))
                .thenReturn(Optional.of(bankCardToSend));
        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankCard));

        service.payForOrder(receiptDto, "login");

        verify(userService).findByLogin(anyString());
        verify(orderService).findOrderById(anyLong());
        verify(bankCardRepository, times(3)).findById(anyLong());
        verify(bankCardRepository).findBankCardByIdAndExpMonthAndExpYearAndCcv(any(), any(), any(), any());

    }

    @Test
    void findBankCardDtoById() throws BankCardException {
        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(BankCard.builder().build()));
        when(bankCardMapper.bankCardToDto(any(BankCard.class)))
                .thenReturn(BankCardDto.builder().build());

        BankCardDto result = service.findBankCardDtoById(1L);

        verify(bankCardRepository).findById(anyLong());
        verify(bankCardMapper).bankCardToDto(any(BankCard.class));
    }

    @Test
    void findBankCardById() throws BankCardException {
        when(bankCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(BankCard.builder().build()));

        BankCard result = service.findBankCardById(1L);

        verify(bankCardRepository).findById(anyLong());
    }
}
