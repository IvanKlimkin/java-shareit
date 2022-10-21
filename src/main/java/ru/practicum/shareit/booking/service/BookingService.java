package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface BookingService {

    List<BookingDto> getAllUserBookings(Long userId, Status status, ShareitPageRequest pageRequest);

    List<BookingDto> getAllUserItemBookings(Long userId, Status status, ShareitPageRequest pageRequest);

    BookingDto addBooking(Long userId, BookingDto bookingDto) throws ValidationException;

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto setStatusByUser(Long userId, Long bookingId, Boolean bookingApprove);

}
