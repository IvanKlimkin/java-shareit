package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private Long id;
    @NotBlank(message = "Необходимо задать email пользователя")
    @Email(message = "Email должен быть корректным")
    private String email;
    @NotBlank(message = "Необходимо задать имя пользователя")
    private String name;
}
