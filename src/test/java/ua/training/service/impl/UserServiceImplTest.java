package ua.training.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.training.api.dto.UserDto;
import ua.training.api.mapper.UserMapper;
import ua.training.domain.user.Role;
import ua.training.domain.user.User;
import ua.training.exception.RegException;
import ua.training.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl service;

    @Test
    void findByLogin() {

        final Long ID = 1L;

        Optional<User> optionalUser = Optional.of(User.builder().id(ID).build());

        when(userRepository.findByLogin(anyString())).thenReturn(optionalUser);

        User result = service.findByLogin("login");

        assertEquals(result.getId(), ID);

        verify(userRepository).findByLogin(anyString());
    }

    @Test
    void findUserById() {

        final Long ID = 1L;

        Optional<User> optionalUser = Optional.of(User.builder().id(ID).build());

        when(userRepository.findUserById(anyLong())).thenReturn(optionalUser);

        User result = service.findUserById(ID);

        assertEquals(result.getId(), ID);

        verify(userRepository).findUserById(anyLong());
    }

    @Test
    void saveNewUserDto() throws RegException {

        final Long ID = 1L;
        final String PASSWORD = "password";

        User user =  User
                .builder()
                .id(ID)
                .password(PASSWORD)
                .build();

        UserDto userDto =  UserDto
                .builder()
                .id(user.getId())
                .password(user.getPassword())
                .build();

        when(userMapper.userDtoToUser(any(UserDto.class))).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

        UserDto result = service.saveNewUserDto(userDto);

        assertEquals(result.getId(), ID);
        assertEquals(result.getPassword(), PASSWORD);

        verify(userRepository).save(any());
        verify(passwordEncoder).encode(anyString());
        verify(userMapper).userToUserDto(any(User.class));
        verify(userMapper).userDtoToUser(any(UserDto.class));

    }

    @Test
    void saveUserException() throws RegException {

        final Long ID = 1L;
        final String PASSWORD = "password";

        User user =  User
                .builder()
                .id(ID)
                .password(PASSWORD)
                .build();

        UserDto userDto =  UserDto
                .builder()
                .id(user.getId())
                .password(user.getPassword())
                .build();

        when(userMapper.userDtoToUser(any(UserDto.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("reg exception"));

        assertThrows(RegException.class,
                () -> {
                    service.saveNewUserDto(userDto);
                });
    }

    @Test
    void findUserDTOById() {

        final Long ID = 1L;

        Optional<User> optionalUser = Optional.of(User.builder().id(ID).build());
        UserDto userDto = UserDto.builder().id(ID).build();

        when(userRepository.findUserById(anyLong())).thenReturn(optionalUser);
        when(userMapper.userToUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = service.findUserDTOById(ID);

        assertEquals(result.getId(), ID);

        verify(userRepository).findUserById(anyLong());
        verify(userMapper).userToUserDto(any(User.class));
    }

    @Test
    void findAllByLoginLike() {

        List<User> users = Arrays.asList(
                User.builder().build(),
                User.builder().build(),
                User.builder().build()
        );

        when(userRepository.findAllByLoginLike(anyString())).thenReturn(users);
        when(userMapper.userToUserDto(any(User.class))).thenReturn(UserDto.builder().build());

        List<UserDto> result = service.findAllByLoginLike("login");

        assertEquals(result.size(), users.size());

        verify(userRepository).findAllByLoginLike(anyString());
        verify(userMapper, times(users.size())).userToUserDto(any(User.class));
    }

    @Test
    void findAllUsers() {

        List<User> users = Arrays.asList(
                User.builder().build(),
                User.builder().build(),
                User.builder().build()
        );

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserDto(any(User.class))).thenReturn(UserDto.builder().build());

        List<UserDto> result = service.findAllUsers();

        assertEquals(result.size(), users.size());

        verify(userRepository).findAll();
        verify(userMapper, times(users.size())).userToUserDto(any(User.class));
    }

    @Test
    void changeRole() {

        final Long ID = 1L;

        Optional<User> user = Optional.of(User.builder().id(ID).role(Role.ROLE_USER).build());

        UserDto userDto = UserDto.builder().id(ID).role(Role.ROLE_ADMIN).build();

        when(userRepository.findUserById(anyLong())).thenReturn(user);
        when(userMapper.userToUserDto(any())).thenReturn(userDto);

        UserDto result = service.changeRole(1L, Role.ROLE_ADMIN);

        verify(userRepository).save(any(User.class));
        verify(userMapper).userToUserDto(any());
        assertEquals(result.getId(), ID);
        assertEquals(result.getRole(), Role.ROLE_ADMIN);
    }

    @Test
    void updateUserInfo() {
        final Long ID = 1L;
        final String PASSWORD = "password";

        User user =  User
                .builder()
                .id(ID)
                .password(PASSWORD)
                .build();

        UserDto userDto =  UserDto
                .builder()
                .id(user.getId())
                .password(user.getPassword())
                .build();

        when(userMapper.userDtoToUser(any(UserDto.class))).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        when(passwordEncoder.encode(any())).thenReturn(PASSWORD);
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = service.updateUserInfo(userDto);

        assertEquals(result.getId(), ID);
        assertEquals(result.getPassword(), PASSWORD);

        verify(userRepository).save(any());
        verify(passwordEncoder).encode(anyString());
        verify(userMapper).userToUserDto(any(User.class));
        verify(userMapper).userDtoToUser(any(UserDto.class));
    }
}
