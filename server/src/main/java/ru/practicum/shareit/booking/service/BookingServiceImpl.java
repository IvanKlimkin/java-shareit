package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final LocalDateTime NOW_MOMENT = LocalDateTime.now();
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<BookingDto> getAllUserBookings(Long userId, State state, ShareitPageRequest pageRequest) {
        User user = userRepository
                .findById(userId).orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<Booking> bookings = new ArrayList<>();
        if (state == State.ALL) {
            bookings = bookingRepository.findBookingsByBooker(
                    user, pageRequest);
        } else if (state == State.FUTURE) {
            bookings = bookingRepository.findBookingsByBookerAndStartAfter(
                    user, NOW_MOMENT, pageRequest);
        } else if (state == State.CURRENT) {
            LocalDateTime currentTime = LocalDateTime.now();
            bookings = bookingRepository.findBookingsByBookerAndStartIsBeforeAndEndIsAfter(
                    user, currentTime/*.plusHours(3)*/, currentTime/*.plusHours(3)*/, pageRequest);
        } else if (state == State.PAST) {
            LocalDateTime currentTime = LocalDateTime.now();
            bookings = bookingRepository.findBookingsByBookerAndEndBefore(
                    user, currentTime/*.plusHours(3)*/, pageRequest);
        } else {
            bookings = bookingRepository.findBookingsByBookerAndState(user, state, pageRequest);
        }
        return bookingMapper.toBookingDto(bookings);
    }

    public List<BookingDto> getAllUserItemBookings(Long userId, State state, ShareitPageRequest pageRequest) {

        User user = userRepository
                .findById(userId).orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<Item> userItems = itemRepository.findItemsByOwner(user);
        List<Booking> userItemBookings = new ArrayList<>();
        if (state == State.ALL) {
            userItemBookings = bookingRepository.findBookingsByItemIn(
                    userItems, pageRequest);
        } else if (state == State.FUTURE) {
            userItemBookings = bookingRepository.findBookingsByItemInAndStartAfter(
                    userItems, NOW_MOMENT, pageRequest);
        } else if (state.equals(State.CURRENT)) {
            LocalDateTime currentTime = LocalDateTime.now();
            userItemBookings = bookingRepository.checkCurrent(userItems, currentTime/*.plusHours(3)*/, pageRequest);
        } else if (state == State.PAST) {
            LocalDateTime currentTime = LocalDateTime.now();
            userItemBookings = bookingRepository.findBookingsByItemInAndEndBefore(
                    userItems, currentTime/*.plusHours(3)*/, pageRequest);
        } else {
            userItemBookings = bookingRepository.findBookingsByItemInAndState(userItems, state, pageRequest);
        }
        return bookingMapper.toBookingDto(userItemBookings);
    }

    @Transactional
    public BookingDto addBooking(Long userId, BookingDto bookingDto) throws ValidationException {
        Booking booking = bookingMapper.toBooking(userId, bookingDto);
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new ServerException("Аренда вещи у самого себя");
        }
        if ((booking.getItem().getAvailable().equals(true))) {
            if (bookingRepository
                    .findBookingsByItemAndEndIsBeforeAndStartIsAfter(
                            booking.getItem(), booking.getStart(), booking.getEnd()).isEmpty()) {
                return bookingMapper.toBookingDto(bookingRepository.save(booking));
            }

        } else {
            throw new ValidationException("Сущность недоступна или заняты даты.");
        }
        return null;
    }

    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServerException("Такое бронирование отсутствует."));
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        } else throw new ServerException("Только хозяин вещи или арендатор вещи могут получать данные");
    }

    @Transactional
    public BookingDto setStatusByUser(Long userId, Long bookingId, Boolean bookingApprove) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServerException("Такое бронирование отсутствует."));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (bookingApprove) {
                if (booking.getState() == State.APPROVED) {
                    throw new ValidationException("Аренда уже подтверждена");
                }
                booking.setState(State.APPROVED);
            } else {
                booking.setState(State.REJECTED);
            }
            bookingRepository.save(booking);
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new ServerException("Только хозяин вещи может изменять статус");
        }

    }
}
