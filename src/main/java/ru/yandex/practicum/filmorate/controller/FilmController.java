package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final short MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final int MIN_FILM_DURATION = 1; // Продолжительность фильма должна быть больше 0 минут
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @PostMapping
    public Film create(@RequestBody Film newFilm) {

        validateFilmName(newFilm);
        log.debug("Фильм {} прошел валидацию имени", newFilm);
        validateFilmDescription(newFilm);
        log.debug("Фильм {} прошел валидацию описания", newFilm);
        validateFilmReleaseDate(newFilm);
        log.debug("Фильм {} прошел валидацию даты релиза", newFilm);
        validateFilmDuration(newFilm);
        log.debug("Фильм {} прошел валидацию продолжительности", newFilm);

        newFilm.setId(getNextId());
        log.debug("Фильму {} присвоен id={}", newFilm, newFilm.getId());

        films.put(newFilm.getId(), newFilm);
        log.debug("В список фильмов добавлен фильм {}", newFilm);

        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.debug("По id={} найден фильм {}", newFilm.getId(), oldFilm);

            validateFilmName(newFilm);
            oldFilm.setName(newFilm.getName());
            log.info("Изменено имя фильма на \"{}\"", oldFilm.getName());

            validateFilmDescription(newFilm);
            oldFilm.setDescription(newFilm.getDescription());
            log.info("Изменено описание фильма на \"{}\"", oldFilm.getDescription());

            validateFilmReleaseDate(newFilm);
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Изменена дата релиза фильма на \"{}\"", oldFilm.getReleaseDate());

            validateFilmDuration(newFilm);
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Изменена продолжительность фильма на \"{}\"", oldFilm.getDuration());

            return oldFilm;
        }

        log.error("Фильм с id=" + newFilm.getId() + " не найден");
        throw new NotFoundException("Фильм с id=" + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    public void validateFilmName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    public void validateFilmDescription(Film film) {
        if (film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            log.error(String.format("Максимальная длина описания фильма %d символов. В запросе длина описания %d символов", MAX_FILM_DESCRIPTION_LENGTH, film.getDescription().length()));
            throw new ValidationException(String.format("Максимальная длина описания фильма %d символов. В запросе длина описания %d символов", MAX_FILM_DESCRIPTION_LENGTH, film.getDescription().length()));
        }
    }

    public void validateFilmReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error(String.format("Дата релиза фильма должна быть после %s", MIN_RELEASE_DATE));
            throw new ValidationException(String.format("Дата релиза фильма должна быть после %s", MIN_RELEASE_DATE));
        }
    }

    public void validateFilmDuration(Film film) {
        if (film.getDuration() < MIN_FILM_DURATION) {
            log.error("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
