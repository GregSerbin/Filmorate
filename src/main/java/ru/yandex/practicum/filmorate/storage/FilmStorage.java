package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {

    void addFilm(Long id, FilmDTO film);

    FilmDTO getFilm(Long id);

    List<FilmDTO> getFilms();

    List<Long> getFilmsIds();

    Boolean containsFilm(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Map<Long, Set<Long>> getFilmsLikes();

}
