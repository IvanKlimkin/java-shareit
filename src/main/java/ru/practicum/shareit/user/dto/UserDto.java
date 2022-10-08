package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class})
    private String email;
    @NotBlank(groups = {Create.class})
    private String name;
}
