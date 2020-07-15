package ua.training.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.exception.BankCardException;
import ua.training.exception.OrderNotFoundException;
import ua.training.service.BankCardService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(BankCardController.BASE_URL)
public class BankCardController {

    public static final String BASE_URL = "/api/user/bank";

    private final BankCardService bankCardService;

    public BankCardController(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BankCardDto> getAllUserBankCards(Authentication user){

        return bankCardService.getAllUserBankCards(user.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardDto addNewBankCard(@Valid @RequestBody BankCardDto bankCardDto, Authentication user)
            throws BankCardException {

        return bankCardService.saveBankCardDTO(bankCardDto, user.getName());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardDto getBankCardById(@PathVariable Long id)
            throws BankCardException {

        return bankCardService.findBankCardDtoById(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public BankCardDto updateBankCard(@RequestBody BankCardDto bankCardDTO)
            throws BankCardException {

        return bankCardService.updateBankCardDTO(bankCardDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardDto deleteBankCardFromUser(@PathVariable Long id, Authentication user)
            throws BankCardException {

        return bankCardService.deleteBankCardConnectionWithUser(id, user.getName());
    }

    @PostMapping(value = "/pay")
    @ResponseStatus(HttpStatus.OK)
    public void payShipment(@RequestBody ReceiptDto receiptDto, Authentication user)
            throws OrderNotFoundException, BankCardException {

        bankCardService.payForOrder(receiptDto, user.getName());
    }
}
