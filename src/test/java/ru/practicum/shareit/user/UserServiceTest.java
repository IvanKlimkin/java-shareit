package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

//@WebMvcTest(UserService.class)
//@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @Mock
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    private UserDto userDto;
    private UserDto userDtoNoId;
    private User user;
    private User userNoId;
    private UserDto userEmailUpdDto;
    private UserDto userNameUpdDto;


    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
        userDto = new UserDto(1L, "user1@email", "user 1");
        userDtoNoId = new UserDto(null, "user1@email", "user 1");
        user = new User(1L, "user1@email", "user 1");
        userNoId = new User(null, "user1@email", "user 1");
        userEmailUpdDto = new UserDto(null, "user1_updated@email", null);
        userNameUpdDto = new UserDto(null, null, "user 1 updated");
    }

    @Test
    void getAllTest() {
        List<User> userList = List.of(user);
        when(userRepository.findAll())
                .thenReturn(userList);

        final List<UserDto> userDtoListCompare = userService.getAllUsers();

        assertEquals(1, userDtoListCompare.size());
        assertEquals(userDto.getEmail(), userDtoListCompare.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        UserDto userFromService = userService.getUserById(1L);

        assertNotNull(userFromService);
        assertEquals(1, userFromService.getId());
        assertEquals(user.getName(), userFromService.getName());
        assertEquals(user.getEmail(), userFromService.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserByIdFailUserTest() throws Exception {
        Exception exp = new ServerException("");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        try {
            UserDto userFromService = userService.getUserById(1L);
        } catch (ServerException e) {
            exp = e;
        }
        assertNotNull(exp);
        assertEquals("Пользователь с таким ID отсутствует.", exp.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveUserTest() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userReceived = userService.addNewUser(userDto);

        assertEquals(1, userReceived.getId());
        assertEquals(user.getName(), userReceived.getName());
        assertEquals(user.getEmail(), userReceived.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void userUpdateUserNameTest() throws Exception {
        User userNameUpd = new User(1L, "user1@email", "user 1 updated");
        when(userRepository.getReferenceById(1L))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(userNameUpd);

        UserDto updUser = userService.updateUser(1L, userNameUpdDto);

        assertEquals(userNameUpd.getId(), updUser.getId());
        assertEquals(userNameUpd.getName(), updUser.getName());
        assertEquals(userNameUpd.getEmail(), updUser.getEmail());

        verify(userRepository, times(1)).getReferenceById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void userUpdateUserEmailTest() throws Exception {
        User userEmailUpd = new User(1L, "user1_updated@email", "user 1");
        when(userRepository.getReferenceById(1L))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(userEmailUpd);

        UserDto updUser = userService.updateUser(1L, userEmailUpdDto);

        assertEquals(userEmailUpd.getId(), updUser.getId());
        assertEquals(userEmailUpd.getName(), updUser.getName());
        assertEquals(userEmailUpd.getEmail(), updUser.getEmail());

        verify(userRepository, times(1)).getReferenceById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void userUpdateUserSameTest() throws Exception {
        UserDto userEmptyUpdDto = new UserDto(null, null, null);
        when(userRepository.getReferenceById(1L))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(user);

        UserDto updUser = userService.updateUser(1L, userEmptyUpdDto);

        assertEquals(user.getId(), updUser.getId());
        assertEquals(user.getName(), updUser.getName());
        assertEquals(user.getEmail(), updUser.getEmail());
        verify(userRepository, times(1)).getReferenceById(1L);
        verify(userRepository, times(1)).save(user);

    }
}
