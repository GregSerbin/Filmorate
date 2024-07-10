package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UserUpdateDTO;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    private final String ADD_USER_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private final String UPDATE_USER_QUERY = "UPDATE users " +
            "SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private final String FIND_USER_BY_ID_QUERY = "SELECT * " +
            "FROM users " +
            "WHERE user_id = ?";
    private final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private final String CHECK_USER_AVAILABILITY_QUERY = "SELECT COUNT(user_id) " +
            "FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private final String UPDATE_FRIENDSHIP_CONFIRMATION_QUERY = "UPDATE friends " +
            "SET friendship_status = true " +
            "WHERE user_id = ? AND friend_id = ?";
    private final String ADD_FRIEND_QUERY = "INSERT INTO friends(user_id, " +
            "friend_id, friendship_status) VALUES (?, ?, false)";
    private final String REMOVE_FRIEND_QUERY = "DELETE FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private final String CHECK_USER_BY_ID_QUERY = "SELECT COUNT(user_id) FROM users WHERE user_id = ?";
    private final String GET_FRIENDS_BY_ID_QUERY = "SELECT user_id " +
            "FROM friends " +
            "WHERE friend_id = ? " +
            "UNION " +
            "SELECT friend_id AS user_id " +
            "FROM friends " +
            "WHERE user_id = ? AND friendship_status = true";


    public User create(User user) {
        log.info("Получен запрос на добавление пользователя {}", user);
        long id = BaseRepository.insert(
                jdbcTemplate,
                ADD_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        log.info("В базу данных добавлен пользователь {}", user);
        return user;
    }

    public UserUpdateDTO update(UserUpdateDTO newUser) {
        log.info("Получен запрос на обновление пользователя {}", newUser);
        int rowsUpdated = jdbcTemplate.update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователя с id=" + newUser.getId() + " не существует");
        }

        return newUser;
    }

    public User getUserById(long userId) {
        log.info("Получен запрос на получение пользователя по id={}", userId);
        return jdbcTemplate.queryForObject(FIND_USER_BY_ID_QUERY, mapper, userId);
    }

    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return jdbcTemplate.query(FIND_ALL_USERS_QUERY, mapper);
    }

    public User addFriend(long userId, long friendId) {
        log.info("Получен запрос на добавление в друзья пользователя с id={} пользователю с id={}", friendId, userId);
        checkUserById(userId);
        checkUserById(friendId);

        Optional<Integer> count1 = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_USER_AVAILABILITY_QUERY,
                Integer.class, userId, friendId));
        Optional<Integer> count2 = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_USER_AVAILABILITY_QUERY,
                Integer.class, friendId, userId));

        if (count1.isEmpty() || count2.isEmpty()) {
            throw new InternalServerException("Ошибка добавления в друзья");
        }

        if (count1.get() > 0) {
            throw new InternalServerException("Заявка на добавление в друзья уже была ранее отправлена");
        } else if (count2.get() > 0) {
            int rowsUpdate = jdbcTemplate.update(UPDATE_FRIENDSHIP_CONFIRMATION_QUERY, friendId, userId);

            if (rowsUpdate == 0) {
                throw new NotFoundException("Пользователя с id=" + friendId + " нет");
            }

            log.info("Заявка на дружбу принята");
            return getUserById(userId);
        }

        log.info("Отправка запроса на добавление в друзья");
        int rowsCreated = jdbcTemplate.update(ADD_FRIEND_QUERY, friendId, userId);

        if (rowsCreated == 0) {
            throw new InternalServerException("Не удалось добавить пользователя в друзья");
        }

        return getUserById(userId);
    }

    public User deleteFriend(long userId, long friendId) {
        log.info("Получен запрос на удаление друга с id={} из друзей пользователя с id={}", friendId, userId);
        checkUserById(userId);
        checkUserById(friendId);
        jdbcTemplate.update(REMOVE_FRIEND_QUERY, friendId, userId);
        return getUserById(userId);
    }

    private void checkUserById(long userId) {
        log.info("Получен запрос на проверку наличия пользователя с id={}", userId);
        Optional<Integer> userOptional = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_USER_BY_ID_QUERY,
                Integer.class, userId));
        if (userOptional.isEmpty()) {
            throw new InternalServerException("Ошибка при попытке найти пользователя с id=" + userId);
        } else if (userOptional.get() == 0) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    public List<User> getFriendsById(long userId) {
        log.info("Получен запрос друзей пользователя с id={}", userId);
        checkUserById(userId);
        List<Integer> friends = jdbcTemplate.queryForList(GET_FRIENDS_BY_ID_QUERY, Integer.class, userId, userId);
        return friends.stream()
                .map(id -> getUserById(id))
                .toList();
    }
}
