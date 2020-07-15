package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.api.mapper.BankCardMapper;
import ua.training.domain.order.Order;
import ua.training.domain.order.Receipt;
import ua.training.domain.order.Status;
import ua.training.domain.user.BankCard;
import ua.training.domain.user.User;
import ua.training.exception.BankCardException;
import ua.training.exception.OrderNotFoundException;
import ua.training.repository.BankCardRepository;
import ua.training.repository.ReceiptRepository;
import ua.training.service.BankCardService;
import ua.training.service.OrderService;
import ua.training.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@PropertySource("classpath:constants.properties")
public class BankCardServiceImpl implements BankCardService {

    private final BankCardRepository bankCardRepository;
    private final UserService userService;
    private final OrderService orderService;
    private final ReceiptRepository receiptRepository;
    private final BankCardMapper bankCardMapper;

    @Value("${constants.ACCOUNT.TO.SEND.MONEY.id}")
    private Long ACCOUNT_TO_SEND_MONEY_ID;

    @Value("${constants.ACCOUNT.TO.SEND.MONEY.expMonth}")
    private Long ACCOUNT_TO_SEND_MONEY_EXP_MONTH;

    @Value("${constants.ACCOUNT.TO.SEND.MONEY.expYear}")
    private Long ACCOUNT_TO_SEND_MONEY_EXP_YEAR;

    @Value("${constants.ACCOUNT.TO.SEND.MONEY.ccv}")
    private Long ACCOUNT_TO_SEND_MONEY_CCV;

    public BankCardServiceImpl(BankCardRepository bankCardRepository, UserService userService,
                               OrderService orderService, ReceiptRepository receiptRepository, BankCardMapper bankCardMapper) {
        this.bankCardRepository = bankCardRepository;
        this.userService = userService;
        this.orderService = orderService;
        this.receiptRepository = receiptRepository;
        this.bankCardMapper = bankCardMapper;
    }

    @Transactional
    public BankCardDto deleteBankCardConnectionWithUser(Long bankId, String login) throws BankCardException {

        BankCard  bankCard = findBankCardById(bankId);

        User user = userService.findByLogin(login);

        bankCard.deleteUser(user);

        return bankCardMapper.bankCardToDto(bankCard);

    }

    @Override
    public List<BankCardDto> getAllUserBankCards(String login) {
        User user = userService.findByLogin(login);
        return bankCardRepository
                .findBankCardByUsers(user).stream()
                .map(bankCardMapper::bankCardToDto)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = BankCardException.class)
    @Override
    public BankCardDto saveBankCardDTO(BankCardDto bankCardDTO, String login) throws BankCardException {
        User user = userService.findByLogin(login);

        Optional<BankCard> optionalBankCard = bankCardRepository.findBankCardByIdAndExpMonthAndExpYearAndCcv(
                bankCardDTO.getId(), bankCardDTO.getExpMonth(), bankCardDTO.getExpYear(), bankCardDTO.getCcv()
        );

        BankCard bankCardToSave = optionalBankCard
                .orElseGet(() -> bankCardMapper.bankCardDtoToBankCard(bankCardDTO));

        user.getCards().add(bankCardToSave);

        try {
            log.error(bankCardToSave.toString());
            BankCard savedBankCard = bankCardRepository.save(bankCardToSave);
            return bankCardMapper.bankCardToDto(savedBankCard);
        } catch (DataIntegrityViolationException e) {
            throw new BankCardException("can not save bank card with id=" + bankCardDTO.getId());
        }

    }

    @Override
    public BankCardDto updateBankCardDTO(BankCardDto bankCardDTO) throws BankCardException {
        BankCard bankCard = findBankCardById(bankCardDTO.getId());
        bankCard.setBalance(bankCardDTO.getBalance());
        return bankCardMapper.bankCardToDto(bankCardRepository.save(bankCard));
    }


    @Override
    public void payForOrder(ReceiptDto receiptDto, String login) throws OrderNotFoundException, BankCardException{

        User user = userService.findByLogin(login);
        receiptDto.setUserId(user.getId());
        Order order = orderService.findOrderById(receiptDto.getOrderId());

        if (order.getStatus().equals(Status.SHIPPED) || order.getStatus().equals(Status.PAID)){
            throw new BankCardException("order is already paid");
        }

        BankCard bankCard =  findBankCardById(receiptDto.getBankCard());

        if (bankCard.getBalance().subtract(order.getShippingPriceInCents()).compareTo(BigDecimal.ZERO) < 0){
            throw  new BankCardException("no enough money");
        }

        Receipt receipt = Receipt.builder()
                .user(user)
                .bankCard(bankCard)
                .order(order)
                .build();

        BankCard bankCardToSend = bankCardRepository
                .findBankCardByIdAndExpMonthAndExpYearAndCcv(ACCOUNT_TO_SEND_MONEY_ID, ACCOUNT_TO_SEND_MONEY_EXP_MONTH,
                        ACCOUNT_TO_SEND_MONEY_EXP_YEAR, ACCOUNT_TO_SEND_MONEY_CCV)
                .orElseThrow(() -> new BankCardException("no bank card to send money"));
        processPaying(receipt, order, bankCardToSend);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {BankCardException.class})
    public void processPaying(Receipt receipt, Order order, BankCard bankCard) throws BankCardException {
        BigDecimal moneyToPay = order.getShippingPriceInCents();

        sendMoney(receipt.getBankCard().getId(), bankCard.getId(), moneyToPay);
        receipt.setPriceInCents(moneyToPay);
        receipt.setCreationDate(LocalDate.now());
        order.setReceipt(receipt);
        receipt.getOrder().setStatus(Status.PAID);
        receiptRepository.save(receipt);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = BankCardException.class)
    public void sendMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) throws BankCardException {
        replenishBankCard(fromAccountId, amount.negate());
        replenishBankCard(toAccountId, amount);

    }

    public void replenishBankCard(Long bankId, BigDecimal moneyToAdd) throws BankCardException {
        BankCard bankCard = findBankCardById(bankId);
        bankCard.setBalance(bankCard.getBalance().add(moneyToAdd));
        bankCardRepository.save(bankCard);
    }

    @Override
    public BankCardDto findBankCardDtoById(Long id) throws BankCardException {
        return bankCardMapper.bankCardToDto(findBankCardById(id));
    }

    @Override
    public BankCard findBankCardById(Long id) throws BankCardException {
        return bankCardRepository
                .findById(id)
                .orElseThrow(() -> new BankCardException("no bank card with id=" + id));
    }
}
