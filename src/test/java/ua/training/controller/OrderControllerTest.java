package ua.training.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.training.api.dto.DestinationDto;
import ua.training.api.dto.OrderDto;
import ua.training.api.dto.OrderTypeDto;
import ua.training.domain.user.Role;
import ua.training.domain.user.User;
import ua.training.exception.ControllerExceptionHandler;
import ua.training.exception.OrderCreateException;
import ua.training.exception.OrderNotFoundException;
import ua.training.service.DestinationService;
import ua.training.service.OrderService;
import ua.training.service.OrderTypeService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest extends AbstractRestControllerTest{

    @Mock
    OrderService orderService;

    @Mock
    Authentication mockPrincipal;

    @Mock
    OrderTypeService orderTypeService;

    @Mock
    DestinationService destinationService;

    @InjectMocks
    OrderController controller;

    MockMvc mockMvc;

    List<OrderDto> orderListDto;

    User user;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();

        OrderDto orderDto1 = OrderDto.builder().id(1L).build();
        OrderDto orderDto2 = OrderDto.builder().id(2L).build();
        OrderDto orderDto3 = OrderDto.builder().id(3L).build();

        orderListDto = Arrays.asList(orderDto1, orderDto2, orderDto3);

        user = User.builder()
                .id(1L)
                .firstName("FirstName")
                .lastName("LastName")
                .login("loginnnnn")
                .role(Role.ROLE_ADMIN)
                .password("3848password")
                .email("email@g.dd").build();
    }

    @Test
    void showAllOrders() throws Exception {

        when(orderService.findAllUserOrders(anyString())).thenReturn(orderListDto);
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(OrderController.BASE_URL + "/show/all")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllUserOrders(any());
    }

    @Test
    void showNotPaidOrders() throws Exception {

        when(orderService.findAllNotPaidUserOrders(anyString())).thenReturn(orderListDto);
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(OrderController.BASE_URL + "/show/not_paid")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllNotPaidUserOrders(anyString());
    }

    @Test
    void showDeliveredOrders() throws Exception {

        when(orderService.findAllDeliveredUserOrders(anyString())).thenReturn(orderListDto);
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(OrderController.BASE_URL + "/show/delivered")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllDeliveredUserOrders(anyString());
    }

    @Test
    void showArchivedOrders() throws Exception {

        when(orderService.findAllArchivedUserOrders(anyString())).thenReturn(orderListDto);
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(OrderController.BASE_URL + "/show/archived")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllArchivedUserOrders(anyString());
    }

    @Test
    void getAllUserOrders() throws Exception {

        when(orderService.findAllUserOrders(any())).thenReturn(orderListDto);
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(OrderController.BASE_URL + "/all_orders")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllUserOrders(any());
    }

    @Test
    void findOrderById() throws Exception {

        OrderDto orderDto = OrderDto.builder().id(1L).build();

        when(mockPrincipal.getName()).thenReturn("login");
        when(orderService.getOrderDtoByIdAndUserId(anyLong(), anyString())).thenReturn(orderDto);

        mockMvc.perform(get(OrderController.BASE_URL + "/find_order/1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(orderService).getOrderDtoByIdAndUserId(anyLong(), anyString());
    }

    @Test
    void createNewOrder() throws Exception {
        when(mockPrincipal.getName()).thenReturn("login");

        OrderDto orderDto = OrderDto.builder()
                .id(2L)
                .description("description")
                .destinationCityFrom("destinationCityFrom")
                .destinationCityTo("to")
                .type("2")
                .weight(BigDecimal.valueOf(77)).build();

        when(orderService.createOrder(any(), any())).thenReturn(orderDto);

        mockMvc.perform(post(OrderController.BASE_URL)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(orderDto))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", equalTo(orderDto.getDescription())));

        verify(orderService).createOrder(any(), any());
    }

    @Test
    void createNewOrderException() throws Exception {
        when(mockPrincipal.getName()).thenReturn("login");

        OrderDto orderDto = OrderDto.builder()
                .id(2L)
                .description("description")
                .destinationCityFrom("destinationCityFrom")
                .destinationCityTo("to")
                .type("2")
                .weight(BigDecimal.valueOf(77)).build();

        when(orderService.createOrder(any(), any())).thenThrow(new OrderCreateException("can not create"));

        mockMvc.perform(post(OrderController.BASE_URL)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(orderDto))
        )
                .andExpect(status().isBadRequest());

        verify(orderService).createOrder(any(), any());
    }

    @Test
    void archiveOrder() throws Exception {

        mockMvc.perform(patch(OrderController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        verify(orderService).moveOrderToArchive(anyLong());
    }

    @Test
    void archiveOrderException() throws Exception {

        when(orderService.moveOrderToArchive(anyLong())).thenThrow(new OrderNotFoundException());

        mockMvc.perform(patch(OrderController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());

        verify(orderService).moveOrderToArchive(anyLong());
    }

    @Test
    void deleteOrder() throws Exception {
        mockMvc.perform(delete(OrderController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        verify(orderService).deleteOrderById(anyLong());
    }

    @Test
    void findAllPaidOrders() throws Exception {

        when(orderService.findAllPaidOrdersDTO()).thenReturn(orderListDto);

        mockMvc.perform(get(OrderController.BASE_URL + "/paid")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllPaidOrdersDTO();
    }

    @Test
    void findAllShippedOrders() throws Exception {

        when(orderService.findAllShippedOrdersDTO()).thenReturn(orderListDto);

        mockMvc.perform(get(OrderController.BASE_URL + "/shipped")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllShippedOrdersDTO();
    }

    @Test
    void findAllDeliveredOrders() throws Exception {

        when(orderService.findAllDeliveredOrdersDto()).thenReturn(orderListDto);

        mockMvc.perform(get(OrderController.BASE_URL + "/delivered")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderListDto.size())));

        verify(orderService).findAllDeliveredOrdersDto();
    }

    @Test
    void findAllTypes() throws Exception {
        List<OrderTypeDto> types = Arrays.asList(
                new OrderTypeDto(),
                new OrderTypeDto()
        );
        when(orderTypeService.getAllOrderTypeDto()).thenReturn(types);

        mockMvc.perform(get(OrderController.BASE_URL + "/types")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(types.size())));

        verify(orderTypeService).getAllOrderTypeDto();
    }

    @Test
    void findDestinationsFrom() throws Exception {
        List<DestinationDto> destinationDtoList = Arrays.asList(
                DestinationDto.builder().cityFrom("from1").build(),
                DestinationDto.builder().cityFrom("from2").build()
        );

        when(destinationService.getAllDestinationDto()).thenReturn(destinationDtoList);

        mockMvc.perform(get(OrderController.BASE_URL + "/destinations_from")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(destinationDtoList.size())));

        verify(destinationService).getAllDestinationDto();
    }

    @Test
    void findDestinationsTo() throws Exception {
        List<DestinationDto> destinationDtoList = Arrays.asList(
                DestinationDto.builder().cityTo("to1").build(),
                DestinationDto.builder().cityTo("to2").build()
        );

        when(destinationService.getAllDestinationDto()).thenReturn(destinationDtoList);

        mockMvc.perform(get(OrderController.BASE_URL + "/destinations_to")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(destinationDtoList.size())));

        verify(destinationService).getAllDestinationDto();
    }
}
