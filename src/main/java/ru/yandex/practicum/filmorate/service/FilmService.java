package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

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

        Long filmId = filmRepository.create(FilmMapper.mapToFilm(filmDTO));
        if (filmId == null) {
            throw new InternalServerException("Не удалось добавить в БД фильм " + filmDTO);
        }

        Film addedFilm = filmRepository.getFilmById(filmId);
        if (filmDTO.getGenres() != null) {
            for (GenreDTO genre : filmDTO.getGenres()) {
                try {
                    filmRepository.addGenreToFilm(addedFilm.getId(), genre.getId());
                } catch (DataAccessException exception) {
                    throw new ValidationException("Не найден жанр с id=" + genre.getId());
                }
            }
        }

        // Обновляем данные по фильму после добавления жанров
        addedFilm = filmRepository.getFilmById(filmId);
        log.info("В БД добавлен фильм {}", addedFilm);
        return FilmMapper.mapToFilmDto(addedFilm);
    }

    public FilmDTO update(FilmUpdateDTO filmDTO) {
        checkMpaRating(filmDTO);
        checkGenre(filmDTO);

        int rowsUpdated = filmRepository.update(FilmMapper.mapToFilm(filmDTO));
        if (rowsUpdated == 0) {
            throw new NotFoundException("Фильма с id=" + filmDTO.getId() + " нет");
        }

        Film film = filmRepository.getFilmById(filmDTO.getId());

        log.info("В базе данных обновлен фильм {}", film);
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
        filmRepository.addLike(filmId, userId);
        return getFilmById(filmId);

    }

    public FilmDTO removeLike(long filmId, long userId) {
        log.info("Получен запрос на удаление лайка фильму с id={} от пользователя с id={}", filmId, userId);
        int rowsRemoved = filmRepository.removeLike(filmId, userId);
        if (rowsRemoved == 0) {
            throw new InternalServerException("Данный пользователь лайк не ставил");
        }
        return getFilmById(filmId);
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
        Optional<Mpa> mpaOptional = mpaRepository.getMpaRatingById(filmDTO.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new ValidationException("Не найден рейтинг mpa с id=" + filmDTO.getMpa().getId());
        }
    }


    public void checkMpaRating(FilmUpdateDTO filmUpdateDTO) {
        Optional<Mpa> mpaOptional = mpaRepository.getMpaRatingById(filmUpdateDTO.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new ValidationException("Не найден рейтинг mpa с id=" + filmUpdateDTO.getMpa().getId());
        }
    }

    public void checkGenre(FilmDTO filmDTO) {
        if (filmDTO.getGenres() != null) {
            for (GenreDTO genre : filmDTO.getGenres()) {
                Optional<Genre> genreOptional = genreRepository.getGenreById(genre.getId());
                if (genreOptional.isEmpty()) {
                    throw new ValidationException("Не найден жанр с id=" + genre.getId());
                }
            }
        }
    }

    public void checkGenre(FilmUpdateDTO filmUpdateDTO) {
        if (filmUpdateDTO.getGenres() != null) {
            for (GenreDTO genre : filmUpdateDTO.getGenres()) {
                Optional<Genre> genreOptional = genreRepository.getGenreById(genre.getId());
                if (genreOptional.isEmpty()) {
                    throw new ValidationException("Не найден жанр с id=" + genre.getId());
                }
            }
        }

    }
}
