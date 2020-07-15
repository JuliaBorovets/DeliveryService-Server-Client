package ua.training.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.training.api.dto.UserDto;
import ua.training.domain.user.Role;
import ua.training.domain.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
class UserMapperTest {

    final Long ID = 1L;

    final String FIRST_NAME = "first name";

    final String LAST_NAME  = "last name";

    final String LOGIN = "login";

    final String EMAIL = "email";

    final Role ROLE = Role.ROLE_USER;

    final String PASSWORD = "password";

    @Test
    void userToUserDto() {
        User user = User.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .login(LOGIN)
                .email(EMAIL)
                .role(ROLE)
                .password(PASSWORD)
                .build();

        UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getLogin(), userDto.getLogin());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getRole(), userDto.getRole());
        assertEquals(user.getPassword(), userDto.getPassword());
    }

    @Test
    void userDtoToUser() {
        UserDto userDto = UserDto.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .login(LOGIN)
                .email(EMAIL)
                .role(ROLE)
                .password(PASSWORD)
                .build();

        User user = UserMapper.INSTANCE.userDtoToUser(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getLogin(), userDto.getLogin());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getRole(), userDto.getRole());
        assertEquals(user.getPassword(), userDto.getPassword());
    }
}
