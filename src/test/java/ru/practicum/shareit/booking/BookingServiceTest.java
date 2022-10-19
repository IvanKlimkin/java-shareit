package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.MyPageRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private MyPageRequest pageRequest;

    private User user;
    private User user2;
    private Item item;

    private Item item2;
    private ItemDto itemUpdName;
    private ItemDto itemUpdAvailable;

    private Booking booking1;

    private Booking booking2;

    private Booking booking;

    private BookingDto bookingDto1;

    private BookingDto bookingDto2;
    private ItemInfoDto itemInfoDto;

    private UserIdDto userIdDto;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, itemRepository, userRepository);
        pageRequest = new MyPageRequest(0, 10, Sort.by("start").descending());
        userIdDto = new UserIdDto(1L);
        user = new User(1L, "user1@email", "user 1");
        user2 = new User(2L, "user2@email", "user 2");
        item = new Item(1L, "Item 1", "Item description", true, user, null);
        item2 = new Item(1L, "Item 1", "Item description", false, user, null);
        itemInfoDto = new ItemInfoDto(1L, "name");
        booking = new Booking(1L, testTime.plusMinutes(5), testTime.plusMinutes(10), item, user, Status.WAITING);
        booking2 = new Booking(2L, testTime.plusMinutes(15), testTime.plusMinutes(20), item, user, Status.APPROVED);
        bookingDto1 = new BookingDto(
                1L, testTime, testTime.plusMinutes(5), userIdDto, itemInfoDto, Status.APPROVED, 1L);
        bookingDto2 = new BookingDto(
                2L, testTime.plusMinutes(10), testTime.plusMinutes(15), userIdDto, itemInfoDto, Status.APPROVED, 1L);
    }

    @Test
    void getAllUserBookingsALL() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBooker(
                user, pageRequest))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.ALL, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingsByBooker(user, pageRequest);
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserBookingsFuture() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndStartAfter(
                any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.FUTURE, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findBookingsByBookerAndStartAfter(
                any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserBookingsCurrent() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.CURRENT, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1))
                .findBookingsByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserBookingsPast() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndEndBefore(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.PAST, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1))
                .findBookingsByBookerAndEndBefore(any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserBookingsOther() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.WAITING, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1))
                .findBookingsByBookerAndStatus(any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserBookingsWrongUser() {
        Exception exp = new ServerException("");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        try {
            final List<BookingDto> BookingsDto = bookingService.getAllUserBookings(1L, Status.WAITING, pageRequest);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Такой User отсутствует", exp.getMessage());
            verify(userRepository, times(1)).findById(any());
        }
    }

    @Test
    void getAllUserItemBookingsALL() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemIn(any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.ALL, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemIn(any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserItemBookingsFuture() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemInAndStartAfter(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.FUTURE, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemInAndStartAfter(any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserItemBookingsCurrent() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.checkCurrent(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.CURRENT, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).checkCurrent(any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserItemBookingsPast() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemInAndEndBefore(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.PAST, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemInAndEndBefore(any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserItemBookingsOther() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwner(user))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItemInAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(List.of(booking)))
                .thenReturn(Collections.singletonList(bookingDto1));

        final List<BookingDto> BookingsDto = bookingService.getAllUserItemBookings(1L, Status.WAITING, pageRequest);

        assertNotNull(BookingsDto);
        assertEquals(1, BookingsDto.size());
        assertEquals(BookingsDto.get(0).getId(), bookingDto1.getId());
        assertEquals(BookingsDto.get(0).getItem(), bookingDto1.getItem());
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findItemsByOwner(user);
        verify(bookingRepository, times(1)).findBookingsByItemInAndStatus(any(), any(), any());
        verify(bookingMapper, times(1)).toBookingDto(List.of(booking));

    }

    @Test
    void getAllUserItemBookingsWrongUser() {
        Exception exp = new ServerException("");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        try {
            final List<BookingDto> BookingsDto =
                    bookingService.getAllUserItemBookings(1L, Status.WAITING, pageRequest);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Такой User отсутствует", exp.getMessage());
            verify(userRepository, times(1)).findById(any());
        }
    }

    @Test
    void addBooking() throws ValidationException {
        when(bookingMapper.toBooking(2L, bookingDto1))
                .thenReturn(booking);
        when(bookingRepository
                .findBookingsByItemAndEndIsBeforeAndStartIsAfter(
                        booking.getItem(), booking.getStart(), booking.getEnd()))
                .thenReturn(Collections.emptyList()); //List.of(booking));
        when(bookingRepository
                .save(booking))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(bookingDto1);

        final BookingDto bookingDto = bookingService.addBooking(2L, bookingDto1);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getItem(), bookingDto1.getItem());
        verify(bookingMapper, times(1)).toBooking(2L, bookingDto1);
        verify(bookingRepository, times(1)).findBookingsByItemAndEndIsBeforeAndStartIsAfter(
                booking.getItem(), booking.getStart(), booking.getEnd());
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toBookingDto(booking);

    }

    @Test
    void addBookingByOUserOwner() throws ValidationException {
        Exception exp = new ServerException("");
        when(bookingMapper.toBooking(1L, bookingDto1))
                .thenReturn(booking);

        try {
            final BookingDto bookingDto = bookingService.addBooking(1L, bookingDto1);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Аренда вещи у самого себя", exp.getMessage());
            verify(bookingMapper, times(1)).toBooking(1L, bookingDto1);
        }
    }

    @Test
    void addBookingNotAllowed() throws ValidationException {
        Exception exp = new ValidationException("");
        when(bookingMapper.toBooking(2L, bookingDto2))
                .thenReturn(booking);


        try {
            final BookingDto bookingDto = bookingService.addBooking(2L, bookingDto2);
        } catch (ValidationException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Сущность недоступна или заняты даты.", exp.getMessage());
            verify(bookingMapper, times(1)).toBooking(2L, bookingDto2);
        }
    }

    @Test
    void getBooking() throws ValidationException {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(bookingDto1);

        final BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getItem(), bookingDto1.getItem());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingMapper, times(1)).toBookingDto(booking);

    }

    @Test
    void getBookingEmpty() throws ValidationException {
        Exception exp = new ServerException("");
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.empty());
        try {
            final BookingDto bookingDto = bookingService.getBookingById(1L, 1L);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Такое бронирование отсутствует.", exp.getMessage());
            verify(bookingRepository, times(1)).findById(1L);
        }

    }

    @Test
    void getBookingByItemOwner() throws ValidationException {
        Exception exp = new ServerException("");

        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));

        try {
            final BookingDto bookingDto = bookingService.getBookingById(2L, 1L);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Только хозяин вещи или арендатор вещи могут получать данные", exp.getMessage());
            verify(bookingRepository, times(1)).findById(1L);
        }
    }

    @Test
    void setStatus() {
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingMapper
                .toBookingDto(booking))
                .thenReturn(bookingDto1);

        final BookingDto bookingDto = bookingService.setStatusByUser(1L, 1L, true);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), bookingDto1.getId());
        assertEquals(bookingDto.getItem(), bookingDto1.getItem());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingMapper, times(1)).toBookingDto(booking);
    }

    @Test
    void setStatusNoBookingExist() {
        Exception exp = new ServerException("");
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.empty());

        try {
            final BookingDto bookingDto = bookingService.setStatusByUser(1L, 1L, true);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Такое бронирование отсутствует.", exp.getMessage());
            verify(bookingRepository, times(1)).findById(1L);
        }
    }

    @Test
    void setStatusWrongOwner() {
        Exception exp = new ServerException("");
        when(bookingRepository
                .findById(1L))
                .thenReturn(Optional.of(booking));

        try {
            final BookingDto bookingDto = bookingService.setStatusByUser(2L, 1L, true);
        } catch (ServerException e) {
            exp = e;

            assertNotNull(exp);
            assertEquals("Только хозяин вещи может изменять статус", exp.getMessage());
            verify(bookingRepository, times(1)).findById(1L);
        }
    }

    @Test
    void setStatusAlreadyApproved() {
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
