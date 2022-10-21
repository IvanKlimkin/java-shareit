package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    private final LocalDateTime testTime = LocalDateTime.of(2022, 12, 01, 00, 00, 00);
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Item item;

    private ItemRequestDto itemRequestDto;

    private ShareitPageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user1@email", "user 1");
        item = new Item(1L, "Item 1", "Item description", true, user, null);
        itemRequestDto = new ItemRequestDto(1L, "request description", user, testTime, List.of(item));
        pageRequest = new ShareitPageRequest(0, 10, Sort.by("created"));
    }

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestService.addNewItemRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1))
                .addNewItemRequest(anyLong(), any());
    }

    @Test
    void getAllTest() throws Exception {
        when(itemRequestService.getAllItemRequestsByUserId(1L))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1))
                .getAllItemRequestsByUserId(1L);
    }

    @Test
    void getAllPageTest() throws Exception {
        when(itemRequestService.getAllItemRequests(1L, pageRequest))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1))
                .getAllItemRequests(1L, pageRequest);
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 2L))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1))
                .getItemRequestById(1L, 2L);
    }

}
