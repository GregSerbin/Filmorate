package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmUpdateDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO create(@Valid @RequestBody FilmDTO newFilm) {
        filmService.create(newFilm);
        return newFilm;
    }

    @PutMapping
    public FilmDTO update(@Valid @RequestBody FilmUpdateDTO newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping
    public Collection<FilmDTO> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDTO getFilm(@Valid @PathVariable final Long id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@Valid @PathVariable("id") Long id,
                        @Valid @PathVariable("userId") Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@Valid @PathVariable("id") Long id,
                           @Valid @PathVariable("userId") Long userId) {
        filmService.removeLike(id, userId);
    }


    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(@Valid @RequestParam(value = "newOwner", required = false, defaultValue = "10") final Integer count) {
        List<FilmDTO> films = filmService.getPopularFilms(count);
        return films;
    }


}
