package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> mapper;

    private final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private final String GET_FILM_GENRES_QUERY = "SELECT * " +
            "FROM films_genre fg " +
            "INNER JOIN genres g ON fg.genre_id = g.genre_id " +
            "WHERE fg.film_id = ?";
    private final String CHECK_IF_GENRE_EXISTS_QUERY = "SELECT COUNT(genre_id) FROM genres WHERE genre_id = ?";


    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение всех жанров");
        return jdbcTemplate.query(GET_ALL_GENRES_QUERY, mapper);
    }

    public Genre getGenreById(int genreId) {
        log.info("Получен запрос на получение жанра с id={}", genreId);
        checkIfGenreExists(genreId);
        return jdbcTemplate.queryForObject(GET_GENRE_BY_ID_QUERY, mapper, genreId);
    }

    public List<Genre> getFilmGenres(long filmId) {
        log.info("Получен запрос на получение жанров фильма с id={}", filmId);
        return jdbcTemplate.query(GET_FILM_GENRES_QUERY, mapper, filmId);
    }

    public void checkIfGenreExists(int id) throws NotFoundException {
        log.info("Получен запрос на проверку жанра с id={}", id);
        Optional<Integer> countGenre = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_IF_GENRE_EXISTS_QUERY,
                Integer.class, id));
        if (countGenre.isEmpty()) {
            throw new InternalServerException("Ошибка проверки наличия жанра c id=" + id);
        } else if (countGenre.get() == 0) {
            throw new NotFoundException("Жанра с id=" + id + " нет");
        }
    }
}
