package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
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
    private static final String CHECK_FILM_QUERY = "SELECT COUNT(user_id) " +
            "FROM likes " +
            "WHERE film_id = ? AND user_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE user_id = ?";
    private static final String FIND_POPULAR_FILM_QUERY = "SELECT film_id " +
            "FROM likes " +
            "GROUP BY film_id " +
            "ORDER BY COUNT(user_id) DESC " +
            "LIMIT ?";

    public Film create(Film film) {
        log.info("Получен запрос на добавление фильма {}", film);
        long id = BaseRepository.insert(
                jdbcTemplate,
                ADD_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );

        film.setId(id);
        log.info("В базу данных добавлен фильм {}", film);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        ADD_FILM_GENRES_QUERY,
                        film.getId(),
                        genre.getId()
                );
                log.info("Для фильма с id={} добавлен жанр {}", film.getId(), genre);
            }
        }
        return film;
    }

    public Film update(Film film) {
        log.info("Получен запрос на обновление фильма {}", film);
        int rowsUpdated = jdbcTemplate.update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException("Такого фильма нет");
        }

        log.info("В базе данных обновлен фильм {}", film);
        return film;
    }

    public void remove(Long id) {
        log.info("Получен запрос на удаление фильма c id={}", id);
        int rowsUpdated = jdbcTemplate.update(
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
        Optional<Integer> count = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_FILM_QUERY, Integer.class, filmId, userId));

        if (count.isEmpty()) {
            throw new InternalServerException("Не удалось поставить лайк");
        }

        if (count.get() > 0) {
            throw new InternalServerException("Данный пользователь уже поставил лайк");
        }

        log.info("Отправка запроса на добавление лайка фильму с id={} от пользователя c id={}", filmId, userId);
        int rowsCreated = jdbcTemplate.update(ADD_LIKE_QUERY, filmId, userId);

        if (rowsCreated == 0) {
            throw new InternalServerException("Не удалось поставить лайк");
        }

        return getFilmById(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        log.info("Отправка запроса на удаление лайка фильму с id={} от пользователя c id={}");
        int rowsDeleted = jdbcTemplate.update(REMOVE_LIKE_QUERY, userId);

        if (rowsDeleted == 0) {
            throw new InternalServerException("Данный пользователь лайк не ставил");
        }

        return getFilmById(filmId);
    }

    public List<Film> findPopularFilms(int count) {
        log.info("Отправка запроса на получение списка самых популярных фильмов");
        List<Integer> popularFilmsIds = jdbcTemplate.queryForList(FIND_POPULAR_FILM_QUERY, Integer.class, count);
        return popularFilmsIds.stream()
                .map(id -> getFilmById(id))
                .collect(Collectors.toList());
    }
}
