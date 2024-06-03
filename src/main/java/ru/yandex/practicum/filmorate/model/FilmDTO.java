package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class FilmDTO {

    private Long id;

    @NotNull(message = "Название фильма не может быть пустым")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull(message = "Описание фильма не может быть пустым")
    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(message = "Описание фильма не может превышать 200 символов", max = 200)
    private String description;

    @NotNull(message = "Дата релиза фильма не может быть пустой")
    @ReleaseDate(message = "Дата релиза фильма должна быть не ранее 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть пустой")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
}
