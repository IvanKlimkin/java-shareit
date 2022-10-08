package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookerAndItemAndStatusAndEndIsBefore(
            User user, Item item, Status status, LocalDateTime end_time);

    List<Booking> findBookingsByBookerAndStatus(User user, Status status);

    List<Booking> findBookingsByBooker(User user, Sort sort);

    List<Booking> findBookingsByBookerAndStartAfter(User user, LocalDateTime start_time, Sort sort);

    List<Booking> findBookingsByBookerAndEndBefore(User user, LocalDateTime stop_time, Sort sort);

    List<Booking> findBookingsByBookerAndStartIsBeforeAndEndIsAfter(
            User user, LocalDateTime cur_time, LocalDateTime cur_time2, Sort sort);

    List<Booking> findBookingsByItemAndEndIsBeforeAndStartIsAfter(Item item, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItemInAndStatus(List<Item> items, Status status);

    List<Booking> findBookingsByItemIn(List<Item> items, Sort sort);

    List<Booking> findBookingsByItemInAndStartAfter(List<Item> items, LocalDateTime start_time, Sort sort);

    List<Booking> findBookingsByItemInAndEndBefore(List<Item> items, LocalDateTime current_time);


    @Query("select b from Booking b where b.item in ?1 and b.start<?2 and b.end>?2")
    List<Booking> checkCurrent(List<Item> items, LocalDateTime current_time);

    Booking findBookingByItemAndStartAfter(Item item, LocalDateTime start_time, Sort sort);

    Booking findBookingByItemAndEndBefore(Item item, LocalDateTime end_time, Sort sort);

}
