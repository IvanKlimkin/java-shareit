package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookerAndItemAndStateAndEndIsBefore(
            User user, Item item, State state, LocalDateTime endTime);

    List<Booking> findBookingsByBookerAndState(User user, State state, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBooker(User user, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndStartAfter(User user, LocalDateTime startTime, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndEndBefore(User user, LocalDateTime stopTime, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByBookerAndStartIsBeforeAndEndIsAfter(
            User user, LocalDateTime curTime, LocalDateTime curTime2, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemAndEndIsBeforeAndStartIsAfter(Item item, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItemInAndState(List<Item> items, State state, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemIn(List<Item> items, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemInAndStartAfter(List<Item> items, LocalDateTime startTime, ShareitPageRequest pageRequest);

    List<Booking> findBookingsByItemInAndEndBefore(List<Item> items, LocalDateTime currentTime, ShareitPageRequest pageRequest);


    @Query("select b from Booking b where b.item in ?1 and b.start<?2 and b.end>?2")
    List<Booking> checkCurrent(List<Item> items, LocalDateTime currentTime, ShareitPageRequest pageRequest);

    Booking findTopByItemAndStartAfter(Item item, LocalDateTime startTime, Sort sort);

    Booking findTopByItemAndEndBefore(Item item, LocalDateTime endTime, Sort sort);

}
