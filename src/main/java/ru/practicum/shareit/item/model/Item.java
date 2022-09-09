package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Item {
    private Long id;
    @NotBlank(message = "Необходимо задать название вещи")
    private String name;
    @NotBlank(message = "Необходимо задать описание вещи")
    private String description;
    @NotNull(message = "Необходимо задать статус вещи")
    private Boolean available;
}
