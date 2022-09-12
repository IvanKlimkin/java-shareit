package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) throws Exception {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) throws Exception {
        if (userService.getAllUsers().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst().isEmpty()) {
            return userService.addNewUser(user);
        } else {
            throw new Exception(String.format("Пользователь с Email=%s уже существует", user.getEmail()));
        }
    }

    @PatchMapping("/{userId}")
    public User patch(@PathVariable Long userId,
                      @RequestBody UserDto userDto) throws Exception {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteItem(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

}
