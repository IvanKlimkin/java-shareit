package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ItemServiceTest {
    private final LocalDateTime testTime = LocalDateTime.of(2022, 01, 01, 00, 00, 00);

    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    private ShareitPageRequest pageRequest;

    private User user;
    private Item item;
    private ItemDto itemUpdName;
    private ItemDto itemUpdAvailable;

    private Booking booking1;

    private Booking booking2;

    private Comment comment;

    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private ItemDto itemDtoRequested;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemMapper = new ItemMapper();
        commentMapper = new CommentMapper();
        itemService = new ItemServiceImpl(
                itemRepository, itemRequestRepository, itemMapper, userRepository, bookingRepository, commentRepository,
                commentMapper);
        pageRequest = new ShareitPageRequest(0, 10, Sort.unsorted());
        user = new User(1L, "user1@email", "user 1");
        item = new Item(1L, "Item 1", "Item description", true, user, null);
        itemUpdName = new ItemDto(null, "Item 1 Updated", null, true, null);
        itemUpdAvailable = new ItemDto(null, "Item 1 update available",
                null, false, null);

        booking1 = new Booking(
                1L,
                testTime.plusMinutes(2),
                testTime.plusHours(2),
                item,
                user,
                Status.APPROVED
        );
        booking2 = new Booking(
                2L,
                testTime.plusHours(3),
                testTime.plusHours(4),
                item,
                user,
                Status.APPROVED
        );
        comment = new Comment(1L, "comment", item, user, testTime);
        commentDto = new CommentDto(1L, "comment", "comment dto name", LocalDateTime.now());
        itemRequest = new ItemRequest(1L, "Item description", user, LocalDateTime.now(), new ArrayList<>());
        itemDto = new ItemDto(1L, "Item 1", "Item description", true, null);
        itemDtoRequested = new ItemDto(1L, "Item 1", "Item description", true, 1L);
        Exception exp = new ServerException("");
    }

    @Test
    void getAllTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findItemsByOwner(any()))
                .thenReturn(Collections.singletonList(item));
        when(bookingRepository.findBookingByItemAndStartAfter(
                any(), any(), any()))
                .thenReturn(booking1);
        when(bookingRepository.findBookingByItemAndEndBefore(
                any(), any(), any()))
                .thenReturn(booking2);
        when(commentRepository.findCommentsByItem(item))
                .thenReturn(Collections.singletonList(comment));


        final List<ItemBookingDto> itemBookings = itemService.getItems(1L, pageRequest);

        assertNotNull(itemBookings);
        assertEquals(1, itemBookings.size());
        assertEquals(itemBookings.get(0).getId(), item.getId());
        assertEquals(itemBookings.get(0).getName(), item.getName());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(any());
        verify(bookingRepository, times(1)).findBookingByItemAndStartAfter(
                any(), any(), any());
        verify(bookingRepository, times(1)).findBookingByItemAndEndBefore(
                any(), any(), any());
        verify(commentRepository, times(1)).findCommentsByItem(item);

    }

    @Test
    void getAllEmptyUserTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.getItems(1L, pageRequest);
        });

        assertNotNull(exp);
        assertEquals("Такой User отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findBookingByItemAndStartAfter(
                any(), any(), any()))
                .thenReturn(booking1);
        when(bookingRepository.findBookingByItemAndEndBefore(
                any(), any(), any()))
                .thenReturn(booking2);
        when(commentRepository.findCommentsByItem(item))
                .thenReturn(Collections.singletonList(comment));


        final ItemBookingDto itemBookingDto = itemService.getItemById(1L, 1L);

        assertNotNull(itemBookingDto);
        assertEquals(itemBookingDto.getId(), item.getId());
        assertEquals(itemBookingDto.getName(), item.getName());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingByItemAndStartAfter(
                any(), any(), any());
        verify(bookingRepository, times(1)).findBookingByItemAndEndBefore(
                any(), any(), any());
        verify(commentRepository, times(1)).findCommentsByItem(item);

    }

    @Test
    void getItemByWrongIdTest() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.getItemById(1L, 1L);
        });

        assertNotNull(exp);
        assertEquals("Вещь с таким ID отсутствует.", exp.getMessage());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void searchItemsTest() {
        when(itemRepository.search("text", pageRequest))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> receivedItems = itemService.searchItems("text", pageRequest);
        assertNotNull(receivedItems);
        assertEquals(receivedItems.size(), 1);
        assertEquals(receivedItems.get(0).getId(), 1L);
        assertEquals(receivedItems.get(0).getName(), item.getName());

        verify(itemRepository, times(1)).search("text", pageRequest);
    }

    @Test
    void addItemTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemDtoReceived = itemService.addNewItem(1L, itemDto);

        assertNotNull(itemDtoReceived);
        assertEquals(itemDtoReceived.getId(), item.getId());
        assertEquals(itemDtoReceived.getName(), item.getName());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addItemRequestedTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemDtoReceived = itemService.addNewItem(1L, itemDtoRequested);

        assertNotNull(itemDtoReceived);
        assertEquals(itemDtoReceived.getId(), item.getId());
        assertEquals(itemDtoReceived.getName(), item.getName());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addItemEmptyUserTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.addNewItem(1L, itemDto);
        });

        assertNotNull(exp);
        assertEquals("Пользователь с таким ID или сущностью отсутствуют", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void addItemEmptyItemTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.addNewItem(1L, itemDtoRequested);
        });

        assertNotNull(exp);
        assertEquals("No such ItemRequest in Db", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void addCommentTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByBookerAndItemAndStatusAndEndIsBefore(
                any(), any(), any(), any()))
                .thenReturn(booking1);
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentReceived = itemService.addNewComment(1L, 1L, commentDto);

        assertNotNull(commentReceived);
        assertEquals(commentReceived.getId(), comment.getId());
        assertEquals(commentReceived.getText(), comment.getText());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingByBookerAndItemAndStatusAndEndIsBefore(
                any(), any(), any(), any());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentEmptyUserTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.addNewComment(1L, 1L, commentDto);
        });

        assertNotNull(exp);
        assertEquals("Пользователь с таким ID отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void addCommentEmptyItemTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.addNewComment(1L, 1L, commentDto);
        });

        assertNotNull(exp);
        assertEquals("Сущность с таким ID отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void addCommentEmptyBookingTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByBookerAndItemAndStatusAndEndIsBefore(
                any(), any(), any(), any()))
                .thenReturn(null);

        ValidationException exp = assertThrows(ValidationException.class, () -> {
            itemService.addNewComment(1L, 1L, commentDto);
        });
        assertNotNull(exp);
        assertEquals("Только пользователь закончивший аренду может оставлять отзывы.", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingByBookerAndItemAndStatusAndEndIsBefore(
                any(), any(), any(), any());
    }

    @Test
    void updateItemTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findItemByIdAndOwner(anyLong(), any()))
                .thenReturn(item);

        ItemDto itemReceived = itemService.updateItem(1L, 1L, itemUpdName);

        assertNotNull(itemReceived);
        assertEquals(itemReceived.getId(), item.getId());
        assertEquals(itemReceived.getName(), itemUpdName.getName());
        assertEquals(itemReceived.getAvailable(), item.getAvailable());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemByIdAndOwner(anyLong(), any());
    }

    @Test
    void updateItemNotAvailableTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findItemByIdAndOwner(anyLong(), any()))
                .thenReturn(item);

        ItemDto itemReceived = itemService.updateItem(1L, 1L, itemUpdAvailable);

        assertNotNull(itemReceived);
        assertEquals(itemReceived.getId(), item.getId());
        assertEquals(itemReceived.getName(), item.getName());
        assertEquals(itemReceived.getAvailable(), itemUpdAvailable.getAvailable());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemByIdAndOwner(anyLong(), any());
    }

    @Test
    void updateItemWrongUserTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto);
        });
        assertNotNull(exp);
        assertEquals("Пользователь с таким ID или сущностью отсутствуют", exp.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateItemWrongItemTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemByIdAndOwner(anyLong(), any()))
                .thenReturn(null);
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto);
        });
        assertNotNull(exp);
        assertEquals("Сущность с таким ID отсутствуют", exp.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemByIdAndOwner(anyLong(), any());
    }

    @Test
    void deleteItemTest() {
        itemService.deleteItem(1L, 1L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

}
