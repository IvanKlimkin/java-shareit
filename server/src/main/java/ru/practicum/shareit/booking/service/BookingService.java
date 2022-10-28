package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface BookingService {

    List<BookingDto> getAllUserBookings(Long userId, State state, ShareitPageRequest pageRequest);

    List<BookingDto> getAllUserItemBookings(Long userId, State state, ShareitPageRequest pageRequest);

    BookingDto addBooking(Long userId, BookingDto bookingDto) throws ValidationException;

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto setStatusByUser(Long userId, Long bookingId, Boolean bookingApprove);

}
