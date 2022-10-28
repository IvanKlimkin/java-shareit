package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserIdDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    private final LocalDateTime testTime = LocalDateTime.of(2022, 12, 01, 00, 00, 00);
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User user;

    private Item item;
    private ItemDto itemDto;
    private ItemBookingDto itemBookingDto;
    private ShareitPageRequest pageRequest;
    private CommentDto commentDto;

    private BookingDto bookingDto1;

    private BookingDto bookingDto2;

    private ItemInfoDto itemInfoDto;

    private UserIdDto userIdDto;

    @BeforeEach
    void beforeEach() {
        userIdDto = new UserIdDto(1L);
        user = new User(1L, "user1@email", "user 1");
        item = new Item(1L, "Item 1", "Item description", true, user, null);
        itemInfoDto = new ItemInfoDto(1L, "name");
        bookingDto1 = new BookingDto(
                1L, testTime, testTime.plusMinutes(5), userIdDto, itemInfoDto, State.APPROVED, 1L);
        bookingDto2 = new BookingDto(
                2L, testTime.plusMinutes(10), testTime.plusMinutes(15), userIdDto, itemInfoDto, State.APPROVED, 1L);
        pageRequest = new ShareitPageRequest(0, 10, Sort.by("start").descending());

    }

    @Test
    void getALLUserBookingsTest() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), any(), any()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()));


        verify(bookingService, times(1))
                .getAllUserBookings(anyLong(), any(), any());
    }

    @Test
    void getALLUserItemBookingsTest() throws Exception {
        when(bookingService.getAllUserItemBookings(1L, State.ALL, pageRequest))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()));


        verify(bookingService, times(1))
                .getAllUserItemBookings(1L, State.ALL, pageRequest);
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(bookingDto1);

        mockMvc.perform(post("/bookings/")
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto1.getId()));


        verify(bookingService, times(1))
                .addBooking(anyLong(), any());
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getBookingById(1L, 1L))
                .thenReturn(bookingDto1);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto1.getId()));

        verify(bookingService, times(1))
                .getBookingById(1L, 1L);
    }

    @Test
    void setStatusTest() throws Exception {
        when(bookingService.setStatusByUser(1L, 1L, true))
                .thenReturn(bookingDto1);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto1.getId()));

        verify(bookingService, times(1))
                .setStatusByUser(1L, 1L, true);
    }

}