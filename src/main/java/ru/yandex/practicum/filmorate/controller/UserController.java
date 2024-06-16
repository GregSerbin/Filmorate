package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserUpdateDTO;
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
        return userService.create(newUser);
    }

    @PutMapping
    public UserDTO update(@Valid @RequestBody UserUpdateDTO newUser) {
        return userService.update(newUser);
    }

    @GetMapping
    public Collection<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@Valid @PathVariable final Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDTO addFriend(@Valid @PathVariable("id") Long id,
                             @Valid @PathVariable("friendId") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDTO removeFriend(@Valid @PathVariable("id") Long id,
                                @Valid @PathVariable("friendId") Long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDTO> getFriends(@Valid @PathVariable("id") Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDTO> getFriends(@Valid @PathVariable("id") Long id,
                                          @Valid @PathVariable("otherId") Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }


}
