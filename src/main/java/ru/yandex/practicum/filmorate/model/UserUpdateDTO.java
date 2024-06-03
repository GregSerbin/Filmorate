package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class UserUpdateDTO {
    @NotNull(message = "Id пользователя не может быть пустым")
    @PositiveOrZero(message = "Id пользователя должно быть больше или равно нулю")
    private Long id;

    @Email(message = "email должен соответствовать формату")
    @NotBlank(message = "email пользователя не может быть пустым")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String login;

    @NotBlank(message = "Имя пользователя не может быть пустым") // Если обновляем имя пользователя, то оно не может быть пустым
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
