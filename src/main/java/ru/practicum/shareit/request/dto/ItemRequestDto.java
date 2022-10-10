package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDto {
    @NotBlank(groups = {Create.class})
    private Long id;

    @NotBlank(groups = {Create.class})
    private String description;

    @NotBlank(groups = {Create.class})
    private User requestor;

    @NotBlank(groups = {Create.class})
    private LocalDateTime created;

}