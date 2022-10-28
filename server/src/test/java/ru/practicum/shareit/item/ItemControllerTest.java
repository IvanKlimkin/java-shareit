package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private ItemBookingDto itemBookingDto;
    private ShareitPageRequest pageRequest;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Item 1", "Item description", true, null);
        itemBookingDto = new ItemBookingDto(
                1L, "ItemBookingDto 1", "ItemBookingDto description", true, null, null,
                Collections.emptyList());
        pageRequest = new ShareitPageRequest(0, 10, Sort.unsorted());
        commentDto = new CommentDto(1L, "text", "author", LocalDateTime.now());
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemService.getItems(1L, pageRequest))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .getItems(1L, pageRequest);
    }

    @Test
    void getItemByIdTest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemService.getItemById(itemId, userId))
                .thenReturn(itemBookingDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemBookingDto.getId()))
                .andExpect(jsonPath("$.name").value(itemBookingDto.getName()))
                .andExpect(jsonPath("$.description").value(itemBookingDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemBookingDto.getAvailable()));


        verify(itemService, times(1))
                .getItemById(itemId, userId);
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItems("text", pageRequest))
                .thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search?text=text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()));


        verify(itemService, times(1))
                .searchItems("text", pageRequest);

    }

    @Test
    void searchItemEmptyTest() throws Exception {

        mockMvc.perform(get("/items/search?text="))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));


        verify(itemService, times(0))
                .searchItems(any(), any());

    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addNewItem(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));


        verify(itemService, times(1))
                .addNewItem(anyLong(), any());
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addNewComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemService, times(1))
                .addNewComment(anyLong(), anyLong(), any());
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));


        verify(itemService, times(1))
                .updateItem(anyLong(), anyLong(), any());
    }

    @Test
    void deleteItemTest() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/items/{itemId}", id)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());


        verify(itemService, times(1))
                .deleteItem(anyLong(), anyLong());
    }

}

