package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDto {
    //@NotBlank(groups = {Create.class})
    private Long id;

    @NotBlank(groups = {Create.class})
    private String description;

    private User requestor;

    private LocalDateTime created;
    private List<Item> items = new ArrayList<>();

}