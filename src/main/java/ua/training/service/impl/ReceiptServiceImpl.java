package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.OrderDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.api.dto.UserDto;
import ua.training.api.mapper.ReceiptMapper;
import ua.training.domain.order.Status;
import ua.training.exception.OrderNotFoundException;
import ua.training.exception.OrderReceiptNotFoundException;
import ua.training.repository.ReceiptRepository;
import ua.training.service.OrderService;
import ua.training.service.ReceiptService;
import ua.training.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final ReceiptMapper receiptMapper;

    public ReceiptServiceImpl(ReceiptRepository receiptRepository, OrderService orderService,
                              UserService userService, ReceiptMapper receiptMapper) {
        this.receiptRepository = receiptRepository;
        this.orderService = orderService;
        this.userService = userService;
        this.receiptMapper = receiptMapper;
    }

    @Override
    public List<ReceiptDto> showAllChecks() {
        return  receiptRepository.findAll().stream()
                .map(receiptMapper::orderCheckToOrderCheckDto)
                .collect(Collectors.toList());
    }


    @Override
    public ReceiptDto showReceiptById(Long receiptId) throws OrderReceiptNotFoundException {
        return receiptMapper.orderCheckToOrderCheckDto(receiptRepository
                .findById(receiptId)
                .orElseThrow(() -> new OrderReceiptNotFoundException("no receipt with id=" + receiptId)));
    }

    @Override
    public List<ReceiptDto>  showChecksByUser(String login) {
        return receiptRepository
                .findAllByUser_Login(login).stream()
                .map(receiptMapper.INSTANCE::orderCheckToOrderCheckDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReceiptDto createCheckDto(Long orderDtoId, BankCardDto bankCardDto, Long userId)
            throws OrderNotFoundException {

        OrderDto orderDto = orderService.getOrderDtoById(orderDtoId);
        UserDto userDto = userService.findUserDTOById(userId);

        return ReceiptDto.builder()
                .orderId(orderDtoId)
                .bankCard(bankCardDto.getId())
                .priceInCents(orderDto.getShippingPriceInCents())
                .userId(userDto.getId())
                .status(Status.NOT_PAID).build();
    }

}
