package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookerAndItemAndStatusAndEndIsBefore(
            User user, Item item, Status status, LocalDateTime endTime);

    List<Booking> findBookingsByBookerAndStatus(User user, Status status, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBooker(User user, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndStartAfter(User user, LocalDateTime startTime, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndEndBefore(User user, LocalDateTime stopTime, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndStartIsBeforeAndEndIsAfter(
            User user, LocalDateTime curTime, LocalDateTime curTime2, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemAndEndIsBeforeAndStartIsAfter(Item item, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItemInAndStatus(List<Item> items, Status status, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemIn(List<Item> items, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemInAndStartAfter(List<Item> items, LocalDateTime startTime, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemInAndEndBefore(List<Item> items, LocalDateTime currentTime, ShareitPageRequest pageRequest);


    @Query("select b from Booking b where b.item in ?1 and b.start<?2 and b.end>?2")
    List<Booking> checkCurrent(List<Item> items, LocalDateTime currentTime, ShareitPageRequest pageRequest);

    Booking findBookingByItemAndStartAfter(Item item, LocalDateTime startTime, Sort sort);

    Booking findBookingByItemAndEndBefore(Item item, LocalDateTime endTime, Sort sort);

}
