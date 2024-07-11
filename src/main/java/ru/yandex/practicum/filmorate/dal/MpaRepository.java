package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mapper;

    private static final String GET_ALL_MPA_RATINGS_QUERY = "SELECT * FROM mpa";
    private static final String GET_MPA_RATING_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?";

    public List<Mpa> getAllMpaRatings() {
        log.info("Получен запрос на получение всех рейтингов mpa");
        return jdbcTemplate.query(GET_ALL_MPA_RATINGS_QUERY, mapper);
    }

    public Optional<Mpa> getMpaRatingById(int mpaId) {
        log.info("Получен запрос на получение рейтинга mpa по id={}", mpaId);
        try {
            Mpa mpa = jdbcTemplate.queryForObject(GET_MPA_RATING_BY_ID_QUERY, mapper, mpaId);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }
}
