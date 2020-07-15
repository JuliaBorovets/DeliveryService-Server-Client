package ua.training.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.training.api.dto.ReceiptDto;
import ua.training.api.dto.StatisticsDto;
import ua.training.exception.OrderNotFoundException;
import ua.training.service.AdminService;
import ua.training.service.ReceiptService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(AdminController.BASE_URL)
public class AdminController {

    public static final String BASE_URL = "/api/admin";

    private final AdminService adminService;
    private final ReceiptService receiptService;

    public AdminController(AdminService adminService, ReceiptService receiptService) {
        this.adminService = adminService;
        this.receiptService = receiptService;
    }

    @PatchMapping("/to_ship/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void shipOneOrder(@PathVariable Long id) throws OrderNotFoundException {

        adminService.shipOrder(id);
    }

    @PatchMapping("/to_deliver/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deliverOneOrder(@PathVariable Long id) throws OrderNotFoundException {

        adminService.deliverOrder(id);
    }

    @PatchMapping("/to_receive/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void receiveOneOrder(@PathVariable Long id) throws OrderNotFoundException {

        adminService.receiveOrder(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public StatisticsDto createGeneralStatistics(){

        return createStatistics();
    }

    @GetMapping("/numbersYear")
    @ResponseStatus(HttpStatus.OK)
    public List<Long> createStatisticsNumbersByYear(){

        return new ArrayList<>(createStatistics().getNumberOfOrdersByForYear().values());
    }

    @GetMapping("/earningsYear")
    @ResponseStatus(HttpStatus.OK)
    public List<BigDecimal> createStatisticsEarningsByYear(){

        return new ArrayList<>(createStatistics().getEarningsOfOrdersByForYear().values());
    }

    private StatisticsDto createStatistics(){
        return adminService.createStatisticsDto();
    }

    @GetMapping("/show_all_receipts")
    @ResponseStatus(HttpStatus.OK)
    public List<ReceiptDto> showAllCheck(){

        return receiptService.showAllChecks();
    }
}

