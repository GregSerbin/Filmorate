package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.UserUpdateDTO;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserDTO newUser) {
        log.info("Получен запрос на добавление пользователя: {}", newUser);
        return userService.create(newUser);
    }

    @PutMapping
    public UserUpdateDTO update(@Valid @RequestBody UserUpdateDTO newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        return userService.update(newUser);
    }

    @GetMapping
    public Collection<UserDTO> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@Valid @PathVariable final Long id) {
        log.info("Получен запрос на получение пользователя с id={}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDTO addFriend(@Valid @PathVariable("id") Long id,
                             @Valid @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос на добавление пользователю с id={} друга с id={}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDTO removeFriend(@Valid @PathVariable("id") Long id,
                                @Valid @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос на удаление у пользователя с id={} друга с id={}", id, friendId);
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDTO> getMutualFriends(@Valid @PathVariable("id") Long id) {
        log.info("Получен запрос на получение всех друзей пользователя с id={}", id);
        return userService.getFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDTO> getMutualFriends(@Valid @PathVariable("id") Long id,
                                                @Valid @PathVariable("otherId") Long otherId) {
        log.info("Получен запрос на получение общих друзей пользователей с id={} и id={}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}
