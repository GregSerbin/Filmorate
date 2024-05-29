package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private User defaultUser;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        defaultUser = User.builder()
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
        User userWithEmptyName = defaultUser.toBuilder()
                .name("")
                .build();

        User userWithNullName = defaultUser.toBuilder()
                .name(null)
                .build();

        assertDoesNotThrow(() -> userController.create(userWithEmptyName));
        assertDoesNotThrow(() -> userController.create(userWithNullName));
    }

    @Test
    public void updateUserShouldNotThrowExceptionWhenUserIsValid() {
        User createdUser = userController.create(defaultUser);
        assertDoesNotThrow(() -> userController.update(createdUser));
    }

    @Test
    public void updatedUserShouldThrowExceptionWhenLoginIsEmptyOrNullOrContainsSpaces() {
        User createdUser = userController.create(defaultUser);

        User updatedUserWithEmptyLogin = createdUser.toBuilder()
                .login("")
                .build();

        User updatedUserWithLoginWithSpaces = createdUser.toBuilder()
                .login("Login With Spaces")
                .build();

        assertThrowsExactly(ValidationException.class, () -> userController.update(updatedUserWithEmptyLogin));
        assertThrowsExactly(ValidationException.class, () -> userController.update(updatedUserWithLoginWithSpaces));
    }

    @Test
    public void updateUserShouldThrowExceptionWhenBirthDateIsInFuture() {
        User createdUser = userController.create(defaultUser);

        User userWithBirthDateInFuture = createdUser.toBuilder()
                .birthday(LocalDate.now().plusDays(10))
                .build();

        assertThrowsExactly(ValidationException.class, () -> userController.update(userWithBirthDateInFuture));
    }

    @Test
    public void getAllFilmsShouldReturnAllFilms() {
        User createdUser = userController.create(defaultUser);
        assertEquals(1, userController.findAll().size(), "Количество добавленных и полученных пользователей должно совпадать");
    }

    @Test
    public void updateShouldThrowExceptionIfUpdateOfNonExistingUser() {
        long idOfNonExistingUser = 100500;
        User nonExistingUser = defaultUser.toBuilder()
                .id(idOfNonExistingUser)
                .build();

        assertThrowsExactly(NotFoundException.class, () -> userController.update(nonExistingUser));
    }
}
