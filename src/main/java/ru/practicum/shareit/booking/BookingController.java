package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadStatusException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(
                                                       name = "state",
                                                       required = false,
                                                       defaultValue = "ALL") String stateParam,
                                               @PositiveOrZero @RequestParam(
                                                       name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(
                                                       name = "size", defaultValue = "10") Integer size) {
        Status status = Status.from(stateParam).orElseThrow(
                () -> new BadStatusException(stateParam));
        final MyPageRequest pageRequest = new MyPageRequest(from, size, Sort.by("start").descending());
        return bookingService.getAllUserBookings(userId, status, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserItemBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(
                                                           name = "state",
                                                           required = false,
                                                           defaultValue = "ALL") String stateParam,
                                                   @PositiveOrZero @RequestParam(
                                                           name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(
                                                           name = "size", defaultValue = "10") Integer size) {
        Status status = Status.from(stateParam).orElseThrow(
                () -> new BadStatusException(stateParam));
        final MyPageRequest pageRequest = new MyPageRequest(from, size, Sort.by("start").descending());
        return bookingService.getAllUserItemBookings(userId, status, pageRequest);
    }

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody BookingDto bookingDto) throws Exception {
        return bookingService.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatusByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam(name = "approved") Boolean bookingApprove) {
        return bookingService.setStatusByUser(userId, bookingId, bookingApprove);
    }

}
