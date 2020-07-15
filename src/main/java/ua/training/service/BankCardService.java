package ua.training.service;

import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.domain.user.BankCard;
import ua.training.exception.BankCardException;
import ua.training.exception.OrderNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface BankCardService {

    void payForOrder(ReceiptDto receiptDto, String login) throws OrderNotFoundException, BankCardException;

    BankCardDto saveBankCardDTO(BankCardDto bankCardDTO, String login) throws BankCardException;

    BankCardDto updateBankCardDTO(BankCardDto bankCardDTO) throws BankCardException;

    BankCardDto deleteBankCardConnectionWithUser(Long bankId, String login) throws BankCardException;

    void replenishBankCard(Long bankId, BigDecimal balance) throws BankCardException;

    List<BankCardDto> getAllUserBankCards(String login);

    BankCardDto findBankCardDtoById(Long id) throws BankCardException;

    BankCard findBankCardById(Long id) throws BankCardException;
}
