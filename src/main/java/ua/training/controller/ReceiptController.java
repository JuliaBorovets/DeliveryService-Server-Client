package ua.training.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.training.api.dto.ReceiptDto;
import ua.training.exception.OrderReceiptNotFoundException;
import ua.training.service.ReceiptService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ReceiptController.BASE_URL)
public class ReceiptController {

    public static final String BASE_URL = "/api/user/receipt";

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReceiptDto> showAllUserReceipts(Authentication user){

        return receiptService.showChecksByUser(user.getName());
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReceiptDto> showCheck(@PathVariable Long id) throws OrderReceiptNotFoundException {

        return Collections.singletonList(receiptService.showReceiptById(id));
    }
}
