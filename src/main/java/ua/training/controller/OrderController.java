package ua.training.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.training.api.dto.DestinationDto;
import ua.training.api.dto.OrderDto;
import ua.training.api.dto.OrderTypeDto;
import ua.training.exception.OrderCreateException;
import ua.training.exception.OrderNotFoundException;
import ua.training.exception.UserNotFoundException;
import ua.training.service.DestinationService;
import ua.training.service.OrderService;
import ua.training.service.OrderTypeService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {

    public static final String BASE_URL = "/api/user/shipments";

    private final OrderService orderService;
    private final OrderTypeService orderTypeService;
    private final DestinationService destinationService;

    public OrderController(OrderService orderService, OrderTypeService orderTypeService,
                           DestinationService destinationService) {
        this.orderService = orderService;
        this.orderTypeService = orderTypeService;
        this.destinationService = destinationService;
    }

    @GetMapping("/show/{filter}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> showUserOrders(@PathVariable String filter, Authentication user){

        List<OrderDto> orderDtoList = new ArrayList<>();
        switch (filter){
            case "all":
                orderDtoList = orderService.findAllUserOrders(user.getName());
                break;
            case "not_paid":
                orderDtoList =  orderService.findAllNotPaidUserOrders(user.getName());
                break;
            case "delivered":
                orderDtoList =  orderService.findAllDeliveredUserOrders(user.getName());
                break;
            case "archived":
                orderDtoList =  orderService.findAllArchivedUserOrders(user.getName());
                break;
        }
        return orderDtoList;
    }

    @GetMapping("/all_orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllUserOrders(Authentication user){

        return orderService.findAllUserOrders(user.getName());
    }

    @GetMapping("/find_order/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> findOrderByIdAndUserId(@PathVariable Long id,  Authentication user)
            throws OrderNotFoundException {

        return Collections.singletonList(orderService.getOrderDtoByIdAndUserId(id, user.getName()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createNewOrder(@Valid @RequestBody OrderDto orderDto, Authentication user)
            throws OrderCreateException, UserNotFoundException {

        return orderService.createOrder(orderDto, user.getName());
    }

    @PatchMapping(value = "/{id}",  produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto archiveOrder(@PathVariable Long id) throws OrderNotFoundException {

        return orderService.moveOrderToArchive(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@PathVariable Long id) throws OrderNotFoundException {

        orderService.deleteOrderById(id);
    }

    @GetMapping("/paid")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> findAllPaidOrders(){

        return orderService.findAllPaidOrdersDTO();
    }

    @GetMapping("/shipped")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> findAllShippedOrders(){

        return orderService.findAllShippedOrdersDTO();
    }

    @GetMapping("/delivered")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> findAllDeliveredOrders(){

        return orderService.findAllDeliveredOrdersDto();
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderTypeDto> findAllTypes(){

        return orderTypeService.getAllOrderTypeDto();
    }


    @GetMapping("/destinations_from")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findDestinationsFrom(){

        return destinationService.getAllDestinationDto()
                .stream()
                .map(DestinationDto::getCityFrom)
                .distinct()
                .collect(Collectors.toList());
    }

    @GetMapping("/destinations_to")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findDestinationsTo(){

        return destinationService.getAllDestinationDto()
                .stream()
                .map(DestinationDto::getCityTo)
                .distinct()
                .collect(Collectors.toList());
    }

}
