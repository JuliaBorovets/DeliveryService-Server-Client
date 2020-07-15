package ua.training.service;

import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.exception.OrderNotFoundException;
import ua.training.exception.OrderReceiptNotFoundException;

import java.util.List;

public interface ReceiptService {

    List<ReceiptDto> showAllChecks();

    ReceiptDto showReceiptById(Long checkId) throws OrderReceiptNotFoundException;

    List<ReceiptDto> showChecksByUser(String login);

    ReceiptDto createCheckDto(Long orderDtoId, BankCardDto bankCardDtoId, Long userId) throws OrderNotFoundException;
}
