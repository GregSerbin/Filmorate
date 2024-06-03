package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmUpdateDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, FilmDTO> films = new HashMap<>();

    @PostMapping
    public FilmDTO create(@Valid @RequestBody FilmDTO newFilm) {

        newFilm.setId(getNextId());
        log.debug("Фильму {} присвоен id={}", newFilm, newFilm.getId());

        films.put(newFilm.getId(), newFilm);
        log.debug("В список фильмов добавлен фильм {}", newFilm);

        return newFilm;
    }

    @PutMapping
    public FilmDTO update(@Valid @RequestBody FilmUpdateDTO newFilm) {

        if (films.containsKey(newFilm.getId())) {
            FilmDTO oldFilm = films.get(newFilm.getId());
            log.debug("По id={} найден фильм {}", newFilm.getId(), oldFilm);

            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
                log.info("Изменено имя фильма на \"{}\"", oldFilm.getName());
            }

            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
                log.info("Изменено описание фильма на \"{}\"", oldFilm.getDescription());
            }

            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.info("Изменена дата релиза фильма на \"{}\"", oldFilm.getReleaseDate());
            }

            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
                log.info("Изменена продолжительность фильма на \"{}\"", oldFilm.getDuration());
            }

            return oldFilm;
        }

        log.error("Фильм с id=" + newFilm.getId() + " не найден");
        throw new NotFoundException("Фильм с id=" + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<FilmDTO> findAll() {
        return films.values();
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
