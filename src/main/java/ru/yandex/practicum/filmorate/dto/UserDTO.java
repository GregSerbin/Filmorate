package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class UserDTO {

    private Long id;

    @Email(message = "email должен соответствовать формату")
    @NotBlank(message = "email пользователя не может быть пустым")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String login;
    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
