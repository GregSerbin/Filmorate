package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.UserUpdateDTO;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDTO create(UserDTO newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("Пользователю {} присвоено имя, равное логину {}", newUser, newUser.getLogin());
        }

        Long userId = userRepository.create(UserMapper.mapToUser(newUser));
        if (userId == null) {
            throw new InternalServerException("Не удалось добавить в БД пользователя " + userId);
        }

        User addedUser = userRepository.getUserById(userId).get();
        log.info("В БД добавлен пользователь {}", addedUser);
        return UserMapper.mapToUserDto(addedUser);
    }

    public UserDTO update(UserUpdateDTO newUser) {
        int rowsUpdated = userRepository.update(newUser);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователя с id=" + newUser.getId() + " не существует");
        }

        User user = userRepository.getUserById(newUser.getId()).get();
        log.info("Обновлен пользователь {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        log.info("Получен список всех пользователей: {}", users);
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDTO getUserById(long userId) {
        log.info("Получен запрос на получение пользователя по id={}", userId);
        Optional<User> userOptional = userRepository.getUserById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return UserMapper.mapToUserDto(userOptional.get());
    }

    public UserDTO addFriend(long userId, long friendId) {
        log.info("Получен запрос на добавление пользователю с id={} друга с id={}", userId, friendId);
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        checkUserById(userId);
        checkUserById(friendId);

        User user = userRepository.addFriend(userId, friendId);
        log.info("Заявка на дружбу успешно отправлена");

        updateFriendshipStatus(userId, friendId);

        return UserMapper.mapToUserDto(user);
    }

    public void updateFriendshipStatus(long userId, long friendId) {
        int numberOfFirstFriendshipQueries = userRepository.getNumberOfFriendshipQueries(userId, friendId);
        int numberOfSecondFriendshipQueries = userRepository.getNumberOfFriendshipQueries(friendId, userId);

        // Если у обоих есть записи о дружбе, то обновляем статус дружбы у обоих друзей
        if (numberOfFirstFriendshipQueries == 1 && numberOfSecondFriendshipQueries == 1) {
            int rowsUpdated = userRepository.updateFriendship(userId, friendId);
            if (rowsUpdated != 1) {
                throw new InternalServerException("Не удалось обновить статус дружбы между пользователями с id=" + userId + " и id=" + friendId);
            }

            rowsUpdated = userRepository.updateFriendship(friendId, userId);
            if (rowsUpdated != 1) {
                throw new InternalServerException("Не удалось обновить статус дружбы между пользователями с id=" + friendId + " и id=" + userId);
            }
        }
    }

    public UserDTO removeFriend(long userId, long friendId) {
        log.info("Получен запрос на удаление пользователя с id={} из друзей пользователя с id={}", friendId, userId);
        if (userId == friendId) {
            throw new ValidationException("Вы не можете удалить самого себя из друзей.");
        }

        checkUserById(userId);
        checkUserById(friendId);

        User user = userRepository.removeFriend(userId, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDTO> getFriendsById(long userId) {
        log.info("Получен запрос на получение друзей пользователя с id={}", userId);

        checkUserById(userId);

        List<User> friends = userRepository.getFriendsById(userId);
        log.info("Список всех друзей получен");
        return friends.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDTO> getMutualFriends(long userId, long friendId) {
        log.info("Получен запрос на получение общих друзей пользователей с id={} и id={}", userId, friendId);

        if (userId == friendId) {
            throw new ValidationException("Нельзя искать общих друзей с самим собой.");
        }

        List<User> userFriends = userRepository.getFriendsById(userId);
        List<User> otherUserFriends = userRepository.getFriendsById(friendId);
        List<User> mutualFriends;

        if (userFriends != null && otherUserFriends != null) {
            List<Long> otherUserFriendsId = userRepository.getFriendsById(friendId)
                    .stream()
                    .map(User::getId)
                    .toList();
            mutualFriends = userFriends
                    .stream()
                    .filter(user -> otherUserFriendsId.contains(user.getId()))
                    .toList();
        } else {
            log.info("У пользователей с id={} и id={} нет общих друзей", userId, friendId);
            throw new NotFoundException("У пользователей с id=" + userId + " и id=" + friendId + " нет общих друзей");
        }

        log.info("Список общих друзей {}", mutualFriends);
        return mutualFriends.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private void checkUserById(long userId) {
        log.info("Получен запрос на проверку наличия пользователя с id={}", userId);
        Optional<User> userOptional = userRepository.getUserById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

}
