package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadStatusException;

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
                                               @RequestParam(
                                                       name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(
                                                       name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam).orElseThrow(
                () -> new BadStatusException(stateParam));
        final ShareitPageRequest pageRequest = new ShareitPageRequest(from, size, Sort.by("start").descending());
        return bookingService.getAllUserBookings(userId, state, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserItemBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(
                                                           name = "state",
                                                           required = false,
                                                           defaultValue = "ALL") String stateParam,
                                                   @RequestParam(
                                                           name = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(
                                                           name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam).orElseThrow(
                () -> new BadStatusException(stateParam));
        final ShareitPageRequest pageRequest = new ShareitPageRequest(from, size, Sort.by("start").descending());
        return bookingService.getAllUserItemBookings(userId, state, pageRequest);
    }

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody BookingDto bookingDto) throws Exception {
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
