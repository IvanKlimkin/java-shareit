package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Item {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;

}
