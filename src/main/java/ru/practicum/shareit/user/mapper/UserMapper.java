package ru.practicum.shareit.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;

    public UserDto toDto(User user) {
        return new UserDto(user.getEmail(), user.getName());
    }

  /*  public User toUser(UserDto userDto) {
        return new User(userRepository.getId(), userDto.getEmail(), userDto.getName());
    }*/
}
