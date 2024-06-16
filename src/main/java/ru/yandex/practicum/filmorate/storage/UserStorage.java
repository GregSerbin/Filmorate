package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserDTO;

import java.util.List;

public interface UserStorage {
    void addUser(Long id, UserDTO user);

    UserDTO getUser(Long id);

    List<UserDTO> getUsers();

    List<Long> getUsersIds();

    Boolean containsUser(Long id);

    UserDTO addFriend(Long userId, Long friendId);

    UserDTO removeFriend(Long userId, Long friendId);

    List<UserDTO> getFriends(Long userId);
}
