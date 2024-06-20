package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserDTO;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, UserDTO> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public void addUser(Long id, UserDTO user) {
        users.put(id, user);
        friends.put(id, new HashSet<>());
    }

    @Override
    public UserDTO getUser(Long id) {
        return users.get(id);
    }

    @Override
    public List<UserDTO> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Long> getUsersIds() {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public Boolean containsUser(Long id) {
        return users.containsKey(id);
    }

    @Override
    public UserDTO addFriend(Long userId, Long friendId) {
        friends.get(userId).add(friendId);
        return users.get(friendId);
    }

    @Override
    public UserDTO removeFriend(Long userId, Long friendId) {
        friends.get(userId).remove(friendId);
        return users.get(friendId);
    }

    @Override
    public List<UserDTO> getFriends(Long userId) {
        List<UserDTO> list = friends.get(userId).stream()
                .map(id -> users.get(id))
                .collect(Collectors.toList());

        return list;
    }
}
