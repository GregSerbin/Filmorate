package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mapper;

    private final String GET_ALL_MPA_RATINGS_QUERY = "SELECT * FROM mpa";
    private final String GET_MPA_RATING_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?";
    private final String CHECK_MPA_RATING_QUERY = "SELECT COUNT(mpa_id) FROM mpa WHERE mpa_id = ?";

    public List<Mpa> getAllMpaRatings() {
        log.info("Получен запрос на получение всех рейтингов mpa");
        return jdbcTemplate.query(GET_ALL_MPA_RATINGS_QUERY, mapper);
    }

    public Mpa getMpaRating(int id) {
        log.info("Получен запрос на получение рейтинга mpa по id={}", id);
        checkMpaRating(id);
        return jdbcTemplate.queryForObject(GET_MPA_RATING_BY_ID_QUERY, mapper, id);
    }

    public void checkMpaRating(int id) throws NotFoundException {
        log.info("Проверка наличия рейтинга в БД c id={}", id);
        Optional<Integer> countMpa = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_MPA_RATING_QUERY,
                Integer.class, id));
        if (countMpa.isEmpty()) {
            throw new InternalServerException("Ошибка проверки наличия рейтинга c id=" + id);
        } else if (countMpa.get() == 0) {
            throw new NotFoundException("Рейтинга с id=" + id + " нет");
        }
    }
}
