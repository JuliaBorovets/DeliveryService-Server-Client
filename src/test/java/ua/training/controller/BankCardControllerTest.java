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
import ua.training.api.dto.BankCardDto;
import ua.training.api.dto.ReceiptDto;
import ua.training.exception.BankCardException;
import ua.training.exception.ControllerExceptionHandler;
import ua.training.service.BankCardService;

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
class BankCardControllerTest extends AbstractRestControllerTest {

    @Mock
    BankCardService bankCardService;

    @Mock
    Authentication mockPrincipal;

    @InjectMocks
    BankCardController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void getAllUserBankCards() throws Exception {

        List<BankCardDto> bankCardDtoList = Arrays.asList(
                BankCardDto.builder().build(),
                BankCardDto.builder().build()
        );

        when(bankCardService.getAllUserBankCards(anyString())).thenReturn(bankCardDtoList);
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(BankCardController.BASE_URL)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bankCardDtoList.size())));

        verify(bankCardService).getAllUserBankCards(anyString());

    }

    @Test
    void addNewBankCard() throws Exception {

        final Long CCV = 123L;
        BankCardDto bankCardDto = BankCardDto.builder().ccv(CCV).build();
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(post(BankCardController.BASE_URL)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bankCardDto))
        )
                .andExpect(status().isCreated());

        verify(bankCardService).saveBankCardDTO(any(), anyString());

    }

    @Test
    void getBankCardById() throws Exception {
        final Long ID = 2L;

        BankCardDto bankCardDto = BankCardDto.builder().id(ID).build();

        when(bankCardService.findBankCardDtoById(anyLong())).thenReturn(bankCardDto);

        mockMvc.perform(get(BankCardController.BASE_URL + "/2")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(2)));

        verify(bankCardService).findBankCardDtoById(anyLong());

    }

    @Test
    void getBankCardByIdException() throws Exception {
        final Long ID = 2L;

        BankCardDto bankCardDto = BankCardDto.builder().id(ID).build();

        when(bankCardService.findBankCardDtoById(anyLong())).thenThrow(new BankCardException("bank card exception"));

        mockMvc.perform(get(BankCardController.BASE_URL + "/2")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());

        verify(bankCardService).findBankCardDtoById(anyLong());

    }

    @Test
    void updateBankCard() throws Exception {

        final Long CCV = 123L;
        BankCardDto bankCardDto = BankCardDto.builder().ccv(CCV).build();

        mockMvc.perform(patch(BankCardController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bankCardDto))
        )
                .andExpect(status().isOk());

        verify(bankCardService).updateBankCardDTO(any());

    }

    @Test
    void deleteBankCardFromUser() throws Exception {

        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(delete(BankCardController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk());

        verify(bankCardService).deleteBankCardConnectionWithUser(anyLong(), anyString());
    }

    @Test
    void payShipment() throws Exception {

        ReceiptDto receiptDto = ReceiptDto.builder().build();

        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(post(BankCardController.BASE_URL + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
                .content(asJsonString(receiptDto))
        )
                .andExpect(status().isOk());

        verify(bankCardService).payForOrder(any(ReceiptDto.class), anyString());
    }
}
