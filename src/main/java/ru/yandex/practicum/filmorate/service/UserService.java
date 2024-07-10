package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.UserUpdateDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
//    private final UserStorage userStorage;

    private final UserRepository userRepository;

    public UserDTO create(UserDTO newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("Пользователю {} присвоено имя, равное логину {}", newUser, newUser.getLogin());
        }

        User user = userRepository.create(UserMapper.mapToUser(newUser));
        log.info("В список пользователей добавлен пользователь {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public UserUpdateDTO update(UserUpdateDTO newUser) {
        UserUpdateDTO user = userRepository.update(newUser);
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        log.info("Получен список всех пользователей: {}", users);
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDTO getUserById(long id) {
        log.info("Получен запрос на получение пользователя по id={}", id);
        User user = userRepository.getUserById(id);
        return UserMapper.mapToUserDto(user);
    }

    public UserDTO addFriend(long userId, long friendId) {
        log.info("Получен запрос на добавление пользователю с id={} друга с id={}", userId, friendId);
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        User user = userRepository.addFriend(userId, friendId);
        log.info("Заявка успешно отправлена");
        return UserMapper.mapToUserDto(user);
    }

    public UserDTO removeFriend(long userId, long friendId) {
        log.info("Получен запрос на удаление пользователя с id={} из друзей пользователя с id={}", friendId, userId);
        if (userId == friendId) {
            throw new ValidationException("Вы не можете удалить самого себя из друзей.");
        }

        User user = userRepository.deleteFriend(userId, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDTO> getFriendsById(long userId) {
        log.info("Получен запрос на получение друзей пользователя с id={}", userId);
        List<User> friends = userRepository.getFriendsById(userId);
        log.info("Список всех друзей получен");
        return friends.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDTO> getMutualFriends(long userId, long friendId) {
        log.info("Получен запрос на получение друзей пользователя с id={}", userId);

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

}
