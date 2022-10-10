package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserIdDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Booking toBooking(Long userId, BookingDto bookingDto) {
        Item item = itemRepository
                .findById(bookingDto.getItemId()).orElseThrow(() -> new ServerException("Такой Item отсутствует"));
        User user = userRepository
                .findById(userId).orElseThrow(() -> new ServerException("Такой User отсутствует"));
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
    }

    public BookingDto toBookingDto(Booking booking) {
        ItemInfoDto itemInfo = new ItemInfoDto(booking.getItem().getId(), booking.getItem().getName());
        UserIdDto booker = new UserIdDto(booking.getBooker().getId());
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booker,
                itemInfo,
                booking.getStatus(),
                itemInfo.getId()
        );
    }

    public List<BookingDto> toBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDto(booking));
        }
        return dtos;
    }

}
