package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.training.api.dto.UserDto;
import ua.training.api.mapper.UserMapper;
import ua.training.configuration.ProjectPasswordEncoder;
import ua.training.domain.user.Role;
import ua.training.domain.user.User;
import ua.training.exception.RegException;
import ua.training.repository.UserRepository;
import ua.training.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private  final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElse(null);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository
                .findUserById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user with id " + id + " not found"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = RegException.class)
    @Override
    public UserDto saveNewUserDto(UserDto userDto) throws RegException {

        User user = userMapper.userDtoToUser(userDto);

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        try {
            userRepository.save(Objects.requireNonNull(user));
            return userMapper.userToUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new RegException("can not save user with login=" + userDto.getLogin());
        }
    }

    @Override
    public UserDto findUserDTOById(Long id) {

        return userMapper.userToUserDto(findUserById(id));
    }

    @Override
    public List<UserDto> findAllByLoginLike(String login) {
        return userRepository.findAllByLoginLike("%" + login + "%").stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto changeRole(Long userId, Role role) {
        User user = findUserById(userId);
        user.setRole(role);
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUserInfo(UserDto userDto) {

        User user = userMapper.userDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userMapper.userToUserDto(userRepository.save(user));
    }
}
