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
import ua.training.api.dto.UserDto;
import ua.training.domain.user.User;
import ua.training.exception.ControllerExceptionHandler;
import ua.training.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest extends AbstractRestControllerTest{

    @Mock
    UserService userService;

    @Mock
    Authentication mockPrincipal;

    @InjectMocks
    UserController controller;

    MockMvc mockMvc;


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();

    }

    @Test
    void register() throws Exception {

        UserDto userDto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .login("loginnnnn")
                .password("3848password")
                .email("email@g.dd").build();

        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))
        )
                .andExpect(status().isCreated());

        verify(userService).saveNewUserDto(any(UserDto.class));
    }

    @Test
    void registerValidation() throws Exception {

        UserDto userDto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .login("loginnnnn")
                .password("password")
                .email("email@g.dd").build();

        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))
        )
                .andExpect(status().is4xxClientError());

    }

    @Test
    void registerException() throws Exception {

        when(userService.findByLogin(anyString())).thenReturn(User.builder().login("login").build());

        UserDto userDto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .login("loginnnnn")
                .password("3848password")
                .email("email@g.dd").build();

        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))
        )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login() throws Exception {
        when(mockPrincipal.getName()).thenReturn("login");

        mockMvc.perform(get(UserController.BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk());

        verify(userService).findByLogin(anyString());
    }

    @Test
    void loginOther() throws Exception {
        when(mockPrincipal.getName()).thenReturn(null);

        mockMvc.perform(get(UserController.BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk());

        verifyNoInteractions(userService);
    }

    @Test
    void changeRole() throws Exception {

        mockMvc.perform(patch(UserController.BASE_URL + "/change/1/ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        verify(userService).changeRole(anyLong(), any());

    }

    @Test
    void findUserByLogin() throws Exception {

        List<UserDto> userListDto = Arrays.asList(
                UserDto.builder().login("login1").build(),
                UserDto.builder().login("login2").build());

        when(userService.findAllByLoginLike(any())).thenReturn(userListDto);

        mockMvc.perform(get(UserController.BASE_URL + "/find/login")
                .contentType(MediaType.APPLICATION_JSON)
        )
               // .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(userListDto.size())));

        verify(userService).findAllByLoginLike(any());
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> userListDto = Arrays.asList(new UserDto(), new UserDto());
        when(userService.findAllUsers()).thenReturn(userListDto);

        mockMvc.perform(get(UserController.BASE_URL + "/users")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(userListDto.size())));

        verify(userService).findAllUsers();
    }

    @Test
    void updateUserInfo() throws Exception {
        UserDto userDto = UserDto.builder().build();
        when(userService.updateUserInfo(any())).thenReturn(UserDto.builder().build());

        mockMvc.perform(put(UserController.BASE_URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))
        )
                .andExpect(status().isOk());

        verify(userService).updateUserInfo(any());
    }

    @Test
    void notUniqueException() throws Exception {

        UserDto userDto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .login("loginnnnn")
                .password("3848password")
                .email("email@g.dd").build();

       User user = User.builder().login("login").build();

        when(userService.findByLogin(anyString())).thenReturn(user);

        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))
        )
                .andExpect(status().isConflict());

        verify(userService).findByLogin(anyString());
    }
}
