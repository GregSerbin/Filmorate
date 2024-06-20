package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserUpdateDTO;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserDTO create(UserDTO newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.debug("Пользователю {} присвоено имя, равное логину {}", newUser, newUser.getLogin());
        }

        newUser.setId(getNextId());
        log.debug("Пользователю {} присвоен id={}", newUser, newUser.getId());

        userStorage.addUser(newUser.getId(), newUser);
        log.debug("В список пользователей добавлен пользователь {}", newUser);

        return newUser;
    }

    public UserDTO update(UserUpdateDTO newUser) {
        if (userStorage.containsUser(newUser.getId())) {
            UserDTO oldUser = userStorage.getUser(newUser.getId());
            log.debug("По id={} найден пользователь {}", newUser.getId(), oldUser);

            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
                log.info("Изменен email на \"{}\"", oldUser.getEmail());
            }

            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
                log.info("Изменен логин на \"{}\"", oldUser.getLogin());
            }

            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
                log.info("Изменено имя на \"{}\"", oldUser.getName());
            }

            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Изменен день рождения на \"{}\"", oldUser.getBirthday());
            }

            return oldUser;
        }

        log.error("Пользователь с id=" + newUser.getId() + " не найден");
        throw new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
    }

    public Collection<UserDTO> findAll() {
        return userStorage.getUsers();
    }

    public UserDTO getUser(Long id) {
        if (!userStorage.containsUser(id)) {
            log.error("Отсутствует пользователь с id={}", id);
            throw new NotFoundException(String.format("Отсутствует пользователь с id=%d", id));
        }

        return userStorage.getUser(id);
    }

    public UserDTO addFriend(Long userId, Long friendId) {
        if (!userStorage.containsUser(userId)) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        if (!userStorage.containsUser(friendId)) {
            log.error("Пользователь с id={} не найден", friendId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", friendId));
        }

        if (userId == friendId) {
            throw new ValidationException("Пользователь не может быть другом самому себе");
        }

        userStorage.addFriend(friendId, userId);
        return userStorage.addFriend(userId, friendId);
    }

    public UserDTO removeFriend(Long userId, Long friendId) {
        if (!userStorage.containsUser(userId)) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        if (!userStorage.containsUser(friendId)) {
            log.error("Пользователь с id={} не найден", friendId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", friendId));
        }

        userStorage.removeFriend(friendId, userId);
        return userStorage.removeFriend(userId, friendId);
    }

    public List<UserDTO> getFriends(Long userId) {
        if (!userStorage.containsUser(userId)) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        return userStorage.getFriends(userId);
    }

    public List<UserDTO> getMutualFriends(Long id, Long otherId) {
        List<UserDTO> friends = userStorage.getFriends(id);
        friends.retainAll(userStorage.getFriends(otherId));
        return friends;
    }


    private long getNextId() {
        long currentMaxId = userStorage.getUsersIds()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
