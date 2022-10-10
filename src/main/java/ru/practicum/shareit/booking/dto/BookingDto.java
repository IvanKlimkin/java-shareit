package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserIdDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@StartBeforeEnd(groups = {Create.class})
public class BookingDto {

    private Long id;
    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    @Future(groups = {Create.class})
    private LocalDateTime end;

    private UserIdDto booker;

    private ItemInfoDto item;

    private Status status = Status.WAITING;
    @NotNull(groups = {Create.class})
    private Long itemId;
}