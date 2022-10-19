package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserDto userDtoNoId;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "user1@email", "user 1");
        userDtoNoId = new UserDto(null, "user1@email", "user 1");
        user = new User(1L, "user1@email", "user 1");
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1))
                .getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        Long id = 1L;
        when(userService.getUserById(id))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));


        verify(userService, times(1))
                .getUserById(id);
    }

    @Test
    void addUser() throws Exception {
        when(userService.addNewUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));

        verify(userService, times(1))
                .addNewUser(any());

    }

    @Test
    void updateUser() throws Exception {
        Long id = 1L;
        UserDto userUpdatedDto = new UserDto(1L, "user1_updated@email", "user 1");

        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userUpdatedDto);

        mockMvc.perform(patch("/users/{userId}", id)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userUpdatedDto.getId()))
                .andExpect(jsonPath("$.email").value(userUpdatedDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userUpdatedDto.getName()));

        verify(userService, times(1))
                .updateUser(anyLong(), any());
    }

    @Test
    void deleteUser() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/users/{userId}", id))
                .andExpect(status().isOk());


        verify(userService, times(1))
                .deleteUser(id);
    }

}

