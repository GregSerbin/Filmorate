package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserUpdateDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private UserDTO defaultUser;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        defaultUser = UserDTO.builder()
                .email("user_email@example.ru")
                .login("user_login")
                .name("User Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    public void createUserShouldNotThrowExceptionWhenUserIsValid() {
        assertDoesNotThrow(() -> userController.create(defaultUser));
    }

    @Test
    public void createUserShouldNotThrowExceptionWhenUserNameIsEmpty() {
        UserDTO userWithEmptyName = defaultUser.toBuilder()
                .name("")
                .build();

        assertDoesNotThrow(() -> userController.create(userWithEmptyName));
    }

    @Test
    public void createUserShouldNotThrowExceptionWhenUserNameIsNull() {
        UserDTO userWithNullName = defaultUser.toBuilder()
                .name(null)
                .build();

        assertDoesNotThrow(() -> userController.create(userWithNullName));
    }

    @Test
    public void updateUserShouldNotThrowExceptionWhenUserIsValid() {
        UserDTO createdUser = userController.create(defaultUser);
        UserUpdateDTO updatedUser = UserUpdateDTO.builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .login(createdUser.getLogin())
                .name(createdUser.getName())
                .birthday(createdUser.getBirthday())
                .build();
        assertDoesNotThrow(() -> userController.update(updatedUser));
    }

    @Test
    public void getAllFilmsShouldReturnAllFilms() {
        UserDTO createdUser = userController.create(defaultUser);
        assertEquals(1, userController.findAll().size(), "Количество добавленных и полученных пользователей должно совпадать");
    }

    @Test
    public void updateShouldThrowExceptionIfUpdateOfNonExistingUser() {
        long idOfNonExistingUser = 100500;
        UserUpdateDTO nonExistingUser = UserUpdateDTO.builder()
                .id(idOfNonExistingUser)
                .email(defaultUser.getEmail())
                .login(defaultUser.getLogin())
                .name(defaultUser.getName())
                .birthday(LocalDate.now().plusDays(10))
                .build();

        assertThrowsExactly(NotFoundException.class, () -> userController.update(nonExistingUser));
    }
}
