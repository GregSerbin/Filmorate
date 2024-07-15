package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO create(@Valid @RequestBody FilmDTO newFilm) {
        log.info("Получен запрос на добавление фильма: {}", newFilm);
        return filmService.create(newFilm);
    }

    @PutMapping
    public FilmDTO update(@Valid @RequestBody FilmUpdateDTO newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        return filmService.update(newFilm);
    }

    @GetMapping
    public Collection<FilmDTO> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDTO getFilmById(@Valid @PathVariable final Long id) {
        log.info("Получен запрос на получение фильма с id={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@Valid @PathVariable("id") Long id,
                        @Valid @PathVariable("userId") Long userId) {
        log.info("Получен запрос на добавление лайка фильму с id={} от пользователя с id={}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@Valid @PathVariable("id") Long id,
                           @Valid @PathVariable("userId") Long userId) {
        log.info("Получен запрос на удаление лайка фильму с id={} от пользователя с id={}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(@Valid @RequestParam(defaultValue = "10") final Integer count) {
        log.info("Получен запрос на получение {} самых популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
