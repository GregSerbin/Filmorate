package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmUpdateDTO;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    public FilmDTO create(FilmDTO film) {
        film.setId(getNextId());
        log.debug("Фильму {} присвоен id={}", film, film.getId());

        filmStorage.addFilm(film.getId(), film);
        log.debug("В список фильмов добавлен фильм {}", film);

        return film;
    }

    public FilmDTO update(FilmUpdateDTO newFilm) {
        if (filmStorage.containsFilm(newFilm.getId())) {
            FilmDTO oldFilm = filmStorage.getFilm(newFilm.getId());
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

    public Collection<FilmDTO> findAll() {
        return filmStorage.getFilms();
    }

    public FilmDTO getFilm(Long id) {
        if (!filmStorage.containsFilm(id)) {
            log.error("Отсутствует фильм с id={}", id);
            throw new NotFoundException(String.format("Отсутствует фильм с id=%d", id));
        }

        return filmStorage.getFilm(id);
    }

    public void addLike(Long filmId, Long userId) {

        if (!filmStorage.containsFilm(filmId)) {
            log.error("Фильм с id={} не найден", filmId);
            throw new NotFoundException(String.format("Фильм с id=%d не найден", filmId));
        }

        if (!userStorage.containsUser(userId)) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {

        if (!filmStorage.containsFilm(filmId)) {
            log.error("Фильм с id={} не найден", filmId);
            throw new NotFoundException(String.format("Фильм с id=%d не найден", filmId));
        }

        if (!userStorage.containsUser(userId)) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        filmStorage.removeLike(filmId, userId);
    }

    public List<FilmDTO> getPopularFilms(Integer count) {
        if (count <= 0) {
            log.error("Запрошено недопустимое количество фильмов {}", count);
            throw new ValidationException(String.format("Запрошено недопустимое количество фильмов %d", count));
        }

        return filmStorage.getFilmsLikes().entrySet()
                .stream()
                .sorted((first, second) -> Integer.compare(second.getValue().size(), first.getValue().size()))
                .map(entry -> entry.getKey())
                .map(id -> filmStorage.getFilm(id))
                .limit(count)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getFilmsIds()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
