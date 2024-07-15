package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmRepository extends BaseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> mapper;

    private static final String ADD_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_FILM_GENRES_QUERY = "MERGE INTO films_genre(film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION , f.RELEASE_DATE , " +
            "f.DURATION , f.MPA_ID , m.NAME AS mpa_name, fg.GENRE_ID , g.NAME AS genre_name " +
            "FROM FILMS f " +
            "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
            "LEFT JOIN FILMS_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID ";
    private static final String GET_FILM_BY_ID_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name AS mpa_name " +
            "FROM films AS f " +
            "INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO likes(film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_POPULAR_FILM_QUERY = "SELECT film_id " +
            "FROM likes " +
            "GROUP BY film_id " +
            "ORDER BY COUNT(user_id) DESC " +
            "LIMIT ?";

    public Long create(Film film) {
        log.info("Получен запрос на добавление фильма {}", film);
        return BaseRepository.insert(
                jdbcTemplate,
                ADD_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
    }

    public int addGenreToFilm(long filmId, int genreId) {
        log.info("Получен запрос на добавление жанра с id={}", filmId);
        return jdbcTemplate.update(ADD_FILM_GENRES_QUERY, filmId, genreId);
    }

    public int update(Film film) {
        log.info("Получен запрос на обновление фильма {}", film);
        return jdbcTemplate.update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
    }

    public void remove(Long id) {
        log.info("Получен запрос на удаление фильма c id={}", id);
        jdbcTemplate.update(
                DELETE_FILM_QUERY,
                id
        );
        log.info("Из базы данных удален фильм с id={}", id);
    }

    public List<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return jdbcTemplate.query(GET_ALL_FILMS_QUERY, mapper);
    }

    public Film getFilmById(long filmId) {
        log.info("Получен запрос на получение фильма по id={}", filmId);
        return jdbcTemplate.queryForObject(GET_FILM_BY_ID_QUERY, mapper, filmId);
    }

    public Film addLike(long filmId, long userId) {
        log.info("Дабавление лайка фильму с id={} от пользователя c id={}", filmId, userId);
        jdbcTemplate.update(ADD_LIKE_QUERY, filmId, userId);
        return getFilmById(filmId);
    }

    public int removeLike(long filmId, long userId) {
        log.info("Отправка запроса на удаление лайка фильму с id={} от пользователя c id={}");
        return jdbcTemplate.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public List<Film> findPopularFilms(int count) {
        log.info("Отправка запроса на получение списка самых популярных фильмов");
        List<Integer> popularFilmsIds = jdbcTemplate.queryForList(FIND_POPULAR_FILM_QUERY, Integer.class, count);
        return popularFilmsIds.stream()
                .map(id -> getFilmById(id))
                .collect(Collectors.toList());
    }
}
