package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookerAndItemAndStatusAndEndIsBefore(
            User user, Item item, Status status, LocalDateTime endTime);

    List<Booking> findBookingsByBookerAndStatus(User user, Status status, MyPageRequest pageRequest);

    List<Booking> findBookingsByBooker(User user, MyPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndStartAfter(User user, LocalDateTime startTime, MyPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndEndBefore(User user, LocalDateTime stopTime, MyPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndStartIsBeforeAndEndIsAfter(
            User user, LocalDateTime curTime, LocalDateTime curTime2, MyPageRequest pageRequest);

    List<Booking> findBookingsByItemAndEndIsBeforeAndStartIsAfter(Item item, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItemInAndStatus(List<Item> items, Status status, MyPageRequest pageRequest);

    List<Booking> findBookingsByItemIn(List<Item> items, MyPageRequest pageRequest);

    List<Booking> findBookingsByItemInAndStartAfter(List<Item> items, LocalDateTime startTime, MyPageRequest pageRequest);

    List<Booking> findBookingsByItemInAndEndBefore(List<Item> items, LocalDateTime currentTime, MyPageRequest pageRequest);


    @Query("select b from Booking b where b.item in ?1 and b.start<?2 and b.end>?2")
    List<Booking> checkCurrent(List<Item> items, LocalDateTime currentTime, MyPageRequest pageRequest);

    Booking findBookingByItemAndStartAfter(Item item, LocalDateTime startTime, Sort sort);

    Booking findBookingByItemAndEndBefore(Item item, LocalDateTime endTime, Sort sort);

}
