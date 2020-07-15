package ua.training.service;

import ua.training.api.dto.UserDto;
import ua.training.domain.user.Role;
import ua.training.domain.user.User;
import ua.training.exception.RegException;

import java.util.List;

public interface UserService {

    UserDto saveNewUserDto(UserDto userDto) throws RegException;

    User findByLogin(String login);

    List<UserDto> findAllUsers();

    UserDto findUserDTOById(Long id);

    User findUserById(Long id);

    UserDto changeRole(Long userId, Role role);

    List<UserDto> findAllByLoginLike(String login);

    UserDto updateUserInfo(UserDto userDto);
}
