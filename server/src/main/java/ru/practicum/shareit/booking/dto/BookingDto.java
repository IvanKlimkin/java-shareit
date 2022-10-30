package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserIdDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private UserIdDto booker;

    private ItemInfoDto item;

    private State status = State.WAITING;

    private Long itemId;
}