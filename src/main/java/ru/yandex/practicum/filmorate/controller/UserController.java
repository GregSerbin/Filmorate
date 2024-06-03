package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserUpdateDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, UserDTO> users = new HashMap<>();

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO newUser) {

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.debug("Пользователю {} присвоено имя равное логину {}", newUser, newUser.getLogin());
        }

        newUser.setId(getNextId());
        log.debug("Пользователю {} присвоен id={}", newUser, newUser.getId());

        users.put(newUser.getId(), newUser);
        log.debug("В список пользователей добавлен пользователь {}", newUser);

        return newUser;
    }

    @PutMapping
    public UserDTO update(@Valid @RequestBody UserUpdateDTO newUser) {

        if (users.containsKey(newUser.getId())) {
            UserDTO oldUser = users.get(newUser.getId());
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

    @GetMapping
    public Collection<UserDTO> findAll() {
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
