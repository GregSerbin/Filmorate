package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UserUpdateDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository extends BaseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> mapper;

    private static final String ADD_USER_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users " +
            "SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * " +
            "FROM users " +
            "WHERE user_id = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String CHECK_USER_AVAILABILITY_QUERY = "SELECT COUNT(user_id) " +
            "FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_FRIENDSHIP_CONFIRMATION_QUERY = "UPDATE friends " +
            "SET friendship_status = true " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String ADD_FRIEND_QUERY = "MERGE INTO friends(user_id, " +
            "friend_id, friendship_status) KEY(user_id, friend_id) VALUES (?, ?, false)";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_BY_ID_QUERY = "SELECT user_id " +
            "FROM friends " +
            "WHERE friend_id = ? " +
            "UNION " +
            "SELECT friend_id AS user_id " +
            "FROM friends " +
            "WHERE user_id = ? AND friendship_status = true";


    public Long create(User user) {
        log.info("Получен запрос на добавление пользователя {}", user);
        return BaseRepository.insert(
                jdbcTemplate,
                ADD_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
    }

    public int update(UserUpdateDTO newUser) {
        log.info("Получен запрос на обновление пользователя {}", newUser);
        return jdbcTemplate.update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getId()
        );
    }

    public Optional<User> getUserById(long userId) {
        log.info("Получен запрос на получение пользователя по id={}", userId);
        try {
            User user = jdbcTemplate.queryForObject(FIND_USER_BY_ID_QUERY, mapper, userId);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return jdbcTemplate.query(FIND_ALL_USERS_QUERY, mapper);
    }

    public User addFriend(long userId, long friendId) {
        log.info("Получен запрос на добавление в друзья пользователя с id={} пользователю с id={}", friendId, userId);
        jdbcTemplate.update(ADD_FRIEND_QUERY, friendId, userId);
        return getUserById(userId).get();
    }

    public int getNumberOfFriendshipQueries(long firstUserId, long secondUserId) {
        return jdbcTemplate.queryForObject(CHECK_USER_AVAILABILITY_QUERY,
                Integer.class, firstUserId, secondUserId);
    }

    public int updateFriendship(long firstUserId, long secondUserId) {
        return jdbcTemplate.queryForObject(UPDATE_FRIENDSHIP_CONFIRMATION_QUERY,
                Integer.class, firstUserId, secondUserId);
    }


    public User removeFriend(long userId, long friendId) {
        log.info("Получен запрос на удаление друга с id={} из друзей пользователя с id={}", friendId, userId);
        jdbcTemplate.update(REMOVE_FRIEND_QUERY, friendId, userId);
        return getUserById(userId).get();
    }

    public List<User> getFriendsById(long userId) {
        log.info("Получен запрос друзей пользователя с id={}", userId);
        List<Integer> friends = jdbcTemplate.queryForList(GET_FRIENDS_BY_ID_QUERY, Integer.class, userId, userId);
        return friends.stream()
                .map(id -> getUserById(id).get())
                .toList();
    }
}
