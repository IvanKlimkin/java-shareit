package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
