package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId) throws Exception;

    UserDto addNewUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto) throws Exception;

    void deleteUser(Long userId);

}
