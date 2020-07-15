package ua.training.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.training.api.dto.UserDto;
import ua.training.domain.user.Role;
import ua.training.domain.user.User;
import ua.training.exception.RegException;
import ua.training.exception.UserAlreadyExistsException;
import ua.training.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {

    public static final String BASE_URL = "/api";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody UserDto user) throws RegException {

        if (userService.findByLogin(user.getLogin()) != null) {
            //Username should be unique.
            throw new UserAlreadyExistsException("user with already exists with login=" + user.getLogin());
        }

        return userService.saveNewUserDto(user);
    }


    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public User login(HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        if (principal == null || principal.getName() == null) {
            return new User();
        }

        return userService.findByLogin(principal.getName());
    }

    @PatchMapping("/change/{id}/{role}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto changeRole(@PathVariable Long id, @PathVariable Role role) {
        return userService.changeRole(id, role);
    }

    @GetMapping("/find/{login}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findUserByLogin(@PathVariable String login){

        return userService.findAllByLoginLike(login);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {

        return userService.findAllUsers();
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUserInfo(@RequestBody UserDto userDto){
        return userService.updateUserInfo(userDto);
    }

}

