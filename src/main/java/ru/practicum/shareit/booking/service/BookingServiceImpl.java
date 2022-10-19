package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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

    public List<BookingDto> getAllUserBookings(Long userId, Status status, MyPageRequest pageRequest) {
        User user = userRepository
                .findById(userId).orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<Booking> bookings = new ArrayList<>();
        if (status == Status.ALL) {
            bookings = bookingRepository.findBookingsByBooker(
                    user, pageRequest);
        } else if (status == Status.FUTURE) {
            bookings = bookingRepository.findBookingsByBookerAndStartAfter(
                    user, NOW_MOMENT, pageRequest);
        } else if (status == Status.CURRENT) {
            LocalDateTime currentTime = LocalDateTime.now();
            bookings = bookingRepository.findBookingsByBookerAndStartIsBeforeAndEndIsAfter(
                    user, currentTime, currentTime, pageRequest);
        } else if (status == Status.PAST) {
            LocalDateTime currentTime = LocalDateTime.now();
            bookings = bookingRepository.findBookingsByBookerAndEndBefore(
                    user, currentTime, pageRequest);
        } else {
            bookings = bookingRepository.findBookingsByBookerAndStatus(user, status, pageRequest);
        }
        return bookingMapper.toBookingDto(bookings);
    }

    public List<BookingDto> getAllUserItemBookings(Long userId, Status status, MyPageRequest pageRequest) {

        User user = userRepository
                .findById(userId).orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<Item> userItems = itemRepository.findItemsByOwner(user);
        List<Booking> userItemBookings = new ArrayList<>();
        if (status == Status.ALL) {
            userItemBookings = bookingRepository.findBookingsByItemIn(
                    userItems, pageRequest);
        } else if (status == Status.FUTURE) {
            userItemBookings = bookingRepository.findBookingsByItemInAndStartAfter(
                    userItems, NOW_MOMENT, pageRequest);
        } else if (status.equals(Status.CURRENT)) {
            LocalDateTime currentTime = LocalDateTime.now();
            userItemBookings = bookingRepository.checkCurrent(userItems, currentTime, pageRequest);
        } else if (status == Status.PAST) {
            LocalDateTime currentTime = LocalDateTime.now();
            userItemBookings = bookingRepository.findBookingsByItemInAndEndBefore(userItems, currentTime, pageRequest);
        } else {
            userItemBookings = bookingRepository.findBookingsByItemInAndStatus(userItems, status, pageRequest);
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
                if (booking.getStatus() == Status.APPROVED) {
                    throw new ValidationException("Аренда уже подтверждена");
                }
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            bookingRepository.save(booking);
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new ServerException("Только хозяин вещи может изменять статус");
        }

    }
}
