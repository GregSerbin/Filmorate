package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {

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
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("По id={} найден пользователь {}", newUser.getId(), oldUser);

            if (newUser.getEmail() != null) {
                validateUserEmail(newUser);
                oldUser.setEmail(newUser.getEmail());
                log.info("Изменен email на \"{}\"", oldUser.getEmail());
            }

            if (newUser.getLogin() != null) {
                validateUserLogin(newUser);
                oldUser.setLogin(newUser.getLogin());
                log.info("Изменен логин на \"{}\"", oldUser.getLogin());
            }

            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
                log.info("Изменено имя на \"{}\"", oldUser.getName());
            }

            if (newUser.getBirthday() != null) {
                validateUserBirthday(newUser);
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Изменен день рождения на \"{}\"", oldUser.getBirthday());
            }

            return oldUser;
        }

        log.error("Пользователь с id=" + newUser.getId() + " не найден");
        throw new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void validateUserEmail(User user) {
        if (user.getEmail() == null ||
                user.getEmail().isBlank() ||
                !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validateUserLogin(User user) {
        if (user.getLogin() == null ||
                user.getLogin().isBlank() ||
                user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private void validateUserBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
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
