package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class FilmUpdateDTO {

    @NotNull(message = "Id фильма не может быть пустым")
    @PositiveOrZero(message = "Id фильма должно быть больше или равно нулю")
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(message = "Описание фильма не может превышать 200 символов", max = 200)
    private String description;

    @ReleaseDate(value = "1895-12-28", message = "Дата релиза фильма должна быть не ранее 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    @NotNull(message = "Рейтинг фильма не может быть пустым")
    private MpaDTO mpa;

    private List<GenreDTO> genres = new ArrayList<>();
}
