package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    public FilmDTO create(FilmDTO filmDTO) {
        checkMpaRating(filmDTO);
        checkGenre(filmDTO);
        Film addedFilm = filmRepository.create(FilmMapper.mapToFilm(filmDTO));
        log.info("Добавлен фильм {}", filmDTO);
        return FilmMapper.mapToFilmDto(addedFilm);
    }

    public FilmDTO update(FilmUpdateDTO filmDTO) {
        checkMpaRating(filmDTO);
        checkGenre(filmDTO);
        Film film = filmRepository.update(FilmMapper.mapToFilm(filmDTO));
        log.info("Фильм обновлен");
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDTO> findAll() {
        List<FilmDTO> films = filmRepository.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
        log.info("Получен список всех фильмов: {}", films);
        return films;
    }

    public FilmDTO getFilmById(long filmId) {
        log.info("Получен запрос на получение фильма с id={}", filmId);
        Film film = filmRepository.getFilmById(filmId);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDTO addLike(long filmId, long userId) {
        log.info("Получен запрос на добавление лайка фильму с id={} от пользователя с id={}", filmId, userId);
        Film film = filmRepository.addLike(filmId, userId);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDTO removeLike(long filmId, long userId) {
        log.info("Получен запрос на удаление лайка фильму с id={} от пользователя с id={}", filmId, userId);
        Film film = filmRepository.removeLike(filmId, userId);
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDTO> getPopularFilms(int count) {
        log.info("Получен запрос на получение {} самых популярных фильмов", count);
        List<Film> popularFilms = filmRepository.findPopularFilms(count);
        log.info("Получен список самых популярных фильмов: {}", popularFilms);
        return popularFilms.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public void checkMpaRating(FilmDTO filmDTO) {
        try {
            if (filmDTO.getMpa() != null) {
                mpaRepository.checkMpaRating(filmDTO.getMpa().getId());
            }
        } catch (NotFoundException e) {
            throw new ValidationException(e.getMessage());
        }
    }


    public void checkMpaRating(FilmUpdateDTO filmUpdateDTO) {
        try {
            if (filmUpdateDTO.getMpa() != null) {
                mpaRepository.checkMpaRating(filmUpdateDTO.getMpa().getId());
            }
        } catch (NotFoundException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public void checkGenre(FilmDTO filmDTO) {
        try {
            if (filmDTO.getGenres() != null) {
                filmDTO.getGenres().forEach(genre -> genreRepository.checkIfGenreExists(genre.getId()));
            }
        } catch (NotFoundException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public void checkGenre(FilmUpdateDTO filmUpdateDTO) {
        try {
            if (filmUpdateDTO.getGenres() != null) {
                filmUpdateDTO.getGenres().forEach(genre -> genreRepository.checkIfGenreExists(genre.getId()));
            }
        } catch (NotFoundException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}
