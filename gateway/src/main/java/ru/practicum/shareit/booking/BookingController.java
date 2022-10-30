package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.BadStatusException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(
                                                             name = "state",
                                                             required = false,
                                                             defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(
                                                             name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(
                                                             name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam).orElseThrow(
                () -> new BadStatusException(stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserItemBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(
                                                                 name = "state",
                                                                 required = false,
                                                                 defaultValue = "ALL") String stateParam,
                                                         @PositiveOrZero @RequestParam(
                                                                 name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(
                                                                 name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam).orElseThrow(
                () -> new BadStatusException(stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllUserItemBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Validated({Create.class}) @RequestBody BookingDto bookingDto) throws Exception {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setStatusByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId,
                                                  @RequestParam(name = "approved") Boolean bookingApprove) {
        log.info("Set state of booking {}, userId={}", bookingId, userId);
        return bookingClient.setStatusByUser(userId, bookingId, bookingApprove);
    }

}
