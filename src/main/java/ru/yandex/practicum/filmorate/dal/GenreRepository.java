package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> mapper;

    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String GET_FILM_GENRES_QUERY = "SELECT * " +
            "FROM films_genre fg " +
            "INNER JOIN genres g ON fg.genre_id = g.genre_id " +
            "WHERE fg.film_id = ?";

    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение всех жанров");
        return jdbcTemplate.query(GET_ALL_GENRES_QUERY, mapper);
    }

    public Optional<Genre> getGenreById(int genreId) {
        log.info("Получен запрос на получение жанра с id={}", genreId);
        try {
            Genre genre = jdbcTemplate.queryForObject(GET_GENRE_BY_ID_QUERY, mapper, genreId);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Genre> getFilmGenres(long filmId) {
        log.info("Получен запрос на получение жанров фильма с id={}", filmId);
        return jdbcTemplate.query(GET_FILM_GENRES_QUERY, mapper, filmId);
    }
}
