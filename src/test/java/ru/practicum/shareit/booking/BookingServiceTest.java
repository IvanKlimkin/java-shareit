package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserIdDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private final LocalDateTime testTime = LocalDateTime.of(2022, 12, 01, 00, 00, 00);

    @Mock
    private BookingService bookingService;
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    private ShareitPageRequest pageRequest;

    private User user;
    private User user2;
    private Item item;

    private Item item2;
    private ItemDto itemUpdName;
    private ItemDto itemUpdAvailable;

    private Booking booking1;

    private Booking booking2;

    private Booking booking;
    private Booking bookingApproved;


    private BookingDto bookingDto1;

    private BookingDto bookingDto2;
    private ItemInfoDto itemInfoDto;

    private UserIdDto userIdDto;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = new BookingMapper(itemRepository, userRepository);
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, itemRepository, userRepository);
        pageRequest = new ShareitPageRequest(0, 10, Sort.by("start").descending());
        userIdDto = new UserIdDto(1L);
        user = new User(1L, "user1@email", "user 1");
        user2 = new User(2L, "user2@email", "user 2");
        item = new Item(1L, "Item 1", "Item description", true, user, null);
        item2 = new Item(1L, "Item 1", "Item description", false, user, null);
        itemInfoDto = new ItemInfoDto(1L, "Item 1");
        booking = new Booking(1L, testTime, testTime.plusMinutes(5), item, user, Status.WAITING);
        bookingApproved = new Booking(1L, testTime, testTime.plusMinutes(5), item, user, Status.APPROVED);
        booking2 = new Booking(2L, testTime.plusMinutes(15), testTime.plusMinutes(20), item, user, Status.APPROVED);
        bookingDto1 = new BookingDto(
                1L, testTime, testTime.plusMinutes(5), userIdDto, itemInfoDto, Status.APPROVED, 1L);
        bookingDto2 = new BookingDto(
                2L, testTime.plusMinutes(10), testTime.plusMinutes(15), userIdDto, itemInfoDto, Status.APPROVED, 1L);
    }

    @Test
    void getAllUserBookingsALLTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBooker(
                user, pageRequest))
                .thenReturn(List.of(booking));


        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.ALL, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingsByBooker(user, pageRequest);

    }

    @Test
    void getAllUserBookingsFutureTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndStartAfter(
                any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.FUTURE, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingsByBookerAndStartAfter(
                any(), any(), any());

    }

    @Test
    void getAllUserBookingsCurrentTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.CURRENT, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1))
                .findBookingsByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());

    }

    @Test
    void getAllUserBookingsPastTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndEndBefore(any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.PAST, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1))
                .findBookingsByBookerAndEndBefore(any(), any(), any());

    }

    @Test
    void getAllUserBookingsOtherTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.WAITING, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1))
                .findBookingsByBookerAndStatus(any(), any(), any());

    }

    @Test
    void getAllUserBookingsWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.getAllUserBookings(1L, Status.WAITING, pageRequest);
        });

        assertNotNull(exp);
        assertEquals("Такой User отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(any());

    }

    @Test
    void getAllUserItemBookingsALLTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemIn(any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.ALL, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemIn(any(), any());

    }

    @Test
    void getAllUserItemBookingsFutureTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemInAndStartAfter(any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.FUTURE, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemInAndStartAfter(any(), any(), any());

    }

    @Test
    void getAllUserItemBookingsCurrentTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.checkCurrent(any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.CURRENT, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).checkCurrent(any(), any(), any());

    }

    @Test
    void getAllUserItemBookingsPastTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemInAndEndBefore(any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.PAST, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemInAndEndBefore(any(), any(), any());

    }

    @Test
    void getAllUserItemBookingsOtherTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemInAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.WAITING, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem().getName(), bookingDto1.getItem().getName());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemInAndStatus(any(), any(), any());

    }

    @Test
    void getAllUserItemBookingsWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.getAllUserItemBookings(1L, Status.WAITING, pageRequest);
        });

        assertNotNull(exp);
        assertEquals("Такой User отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void addBookingTest() throws ValidationException {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository
                .findBookingsByItemAndEndIsBeforeAndStartIsAfter(
                        booking.getItem(), booking.getStart(), booking.getEnd()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository
                .save(any()))
                .thenReturn(booking);


        final BookingDto bookingDto = bookingService.addBooking(2L, bookingDto1);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getItem().getName(), bookingDto1.getItem().getName());
        verify(bookingRepository, times(1)).findBookingsByItemAndEndIsBeforeAndStartIsAfter(
                booking.getItem(), booking.getStart(), booking.getEnd());
        verify(bookingRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());

    }

    @Test
    void addBookingByOUserOwnerTest() throws ValidationException {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.addBooking(1L, bookingDto1);
        });

        assertNotNull(exp);
        assertEquals("Аренда вещи у самого себя", exp.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void addBookingNotAllowedTest() {
        Exception exp = new ValidationException("");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);


        try {
            final BookingDto bookingDto = bookingService.addBooking(2L, bookingDto1);
        } catch (ValidationException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Сущность недоступна или заняты даты.", exp.getMessage());
            verify(userRepository, times(1)).findById(anyLong());
            verify(itemRepository, times(1)).findById(anyLong());
            verify(bookingRepository, times(1)).save(any());
        }
    }

    @Test
    void getBookingTest() throws ValidationException {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));

        final BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getItem().getName(), bookingDto1.getItem().getName());
        verify(bookingRepository, times(1)).findById(1L);

    }

    @Test
    void getBookingEmptyTest() {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.getBookingById(1L, 1L);
        });

        assertNotNull(exp);
        assertEquals("Такое бронирование отсутствует.", exp.getMessage());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getBookingByItemOwnerTest() {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));

        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.getBookingById(2L, 1L);
        });

        assertNotNull(exp);
        assertEquals("Только хозяин вещи или арендатор вещи могут получать данные", exp.getMessage());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void setStatusTest() {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));

        final BookingDto bookingDto = bookingService.setStatusByUser(1L, 1L, true);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getItem().getName(), bookingDto1.getItem().getName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void setStatusNoBookingExistTest() {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.empty());

        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.setStatusByUser(1L, 1L, true);
        });

        assertNotNull(exp);
        assertEquals("Такое бронирование отсутствует.", exp.getMessage());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void setStatusWrongOwnerTest() {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));

        ServerException exp = assertThrows(ServerException.class, () -> {
            bookingService.setStatusByUser(2L, 1L, true);
        });

        assertNotNull(exp);
        assertEquals("Только хозяин вещи может изменять статус", exp.getMessage());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void setStatusAlreadyApprovedTest() {
        Exception exp = new ValidationException("");
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));


        try {
            final BookingDto bookingDto = bookingService.setStatusByUser(1L, 1L, true);
        } catch (ru.practicum.shareit.exception.ValidationException e) {
            exp = e;
            assertNotNull(exp);
            assertEquals("Аренда уже подтверждена", exp.getMessage());
            verify(bookingRepository, times(1)).findById(1L);
        }
    }
}
