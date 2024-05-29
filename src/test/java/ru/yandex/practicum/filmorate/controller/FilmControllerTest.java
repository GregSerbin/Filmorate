package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Film defaultFilm;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        defaultFilm = Film.builder()
                .name("Film Name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .build();
    }

    @Test
    public void createFilmShouldNotThrowExceptionWhenFilmIsValid() {
        assertDoesNotThrow(() -> filmController.create(defaultFilm));
    }

    @Test
    public void createFilmShouldThrowExceptionWhenNameIsEmptyOrNull() {
        Film filmWithEmptyName = defaultFilm.toBuilder()
                .name("") // Имя фильма не должно быть пустым
                .build();

        Film filmWithNullName = defaultFilm.toBuilder()
                .name(null) // Имя фильма не должно быть пустым
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.create(filmWithEmptyName));
        assertThrowsExactly(ValidationException.class, () -> filmController.create(filmWithNullName));
    }

    @Test
    public void createFilmShouldThrowExceptionWhenDescriptionIsOver200Characters() {

        short descriptionLengthMoreThan200Characters = 300;
        byte[] array = new byte[descriptionLengthMoreThan200Characters];
        new Random().nextBytes(array);
        String description = new String(array, Charset.forName("UTF-8"));

        Film film = defaultFilm.toBuilder()
                .description(description) // Описание фильма не должно быть более 200 символов
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void createFilmShouldThrowExceptionWhenReleaseDateIsEarlier25_12_1895() {
        Film film = defaultFilm.toBuilder()
                .releaseDate(LocalDate.of(1800, 1, 1)) // Релиз фильма не должен быть ранее 25 декабря 1895 года
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void createFilmShouldThrowExceptionWhenFilmDurationIsNotPositive() {
        Film filmWithNegativeDuration = defaultFilm.toBuilder()
                .duration(-1) // Продолжительность фильма не должна быть равной нулю или отрицательной
                .build();

        Film filmWithZeroDuration = defaultFilm.toBuilder()
                .duration(0) // Продолжительность фильма не должна быть равной нулю или отрицательной
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.create(filmWithNegativeDuration));
        assertThrowsExactly(ValidationException.class, () -> filmController.create(filmWithZeroDuration));
    }

    @Test
    public void updateFilmShouldNotThrowExceptionWhenFilmIsValid() {
        Film createdFilm = filmController.create(defaultFilm);
        Film updatedFilm = createdFilm.toBuilder()
                .name("Updated Name")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .build();
        assertDoesNotThrow(() -> filmController.update(updatedFilm));
    }

    @Test
    public void updateFilmShouldThrowExceptionWhenNameIsEmptyOrNull() {
        Film createdFilm = filmController.create(defaultFilm);
        Film filmWithEmptyName = createdFilm.toBuilder()
                .name("") // Имя фильма не должно быть пустым
                .build();

        Film filmWithNullName = createdFilm.toBuilder()
                .name(null) // Имя фильма не должно быть пустым
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.update(filmWithEmptyName));
        assertThrowsExactly(ValidationException.class, () -> filmController.update(filmWithNullName));
    }

    @Test
    public void updateFilmShouldThrowExceptionWhenDescriptionIsOver200Characters() {
        short descriptionLengthMoreThan200Characters = 300;
        byte[] array = new byte[descriptionLengthMoreThan200Characters];
        new Random().nextBytes(array);
        String description = new String(array, Charset.forName("UTF-8"));

        Film createdFilm = filmController.create(defaultFilm);
        Film updatedFilm = createdFilm.toBuilder()
                .description(description) // Описание фильма не должно быть более 200 символов
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.update(updatedFilm));
    }

    @Test
    public void updateFilmShouldThrowExceptionWhenReleaseDateIsEarlier25_12_1895() {
        Film createdFilm = filmController.create(defaultFilm);
        Film updatedFilm = createdFilm.toBuilder()
                .releaseDate(LocalDate.of(1800, 1, 1)) // Релиз фильма не должен быть ранее 25 декабря 1895 года
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.create(updatedFilm));
    }

    @Test
    public void updateFilmShouldThrowExceptionWhenFilmDurationIsNotPositive() {
        Film createdFilm = filmController.create(defaultFilm);
        Film filmWithNegativeDuration = createdFilm.toBuilder()
                .duration(-1) // Продолжительность фильма не должна быть равной нулю или отрицательной
                .build();

        Film filmWithZeroDuration = createdFilm.toBuilder()
                .duration(0) // Продолжительность фильма не должна быть равной нулю или отрицательной
                .build();

        assertThrowsExactly(ValidationException.class, () -> filmController.update(filmWithNegativeDuration));
        assertThrowsExactly(ValidationException.class, () -> filmController.update(filmWithZeroDuration));
    }

    @Test
    public void getAllFilmsShouldReturnAllFilms() {
        Film createdFilm = filmController.create(defaultFilm);
        assertEquals(1, filmController.findAll().size(), "Количество добавленных и полученных фильмов должно совпадать");
    }

    @Test
    public void updateShouldThrowExceptionIfUpdateOfNonExistingFilm() {
        long idOfNonExistingFilm = 100500;
        Film nonExistingFilm = defaultFilm.toBuilder()
                .id(idOfNonExistingFilm)
                .build();

        assertThrowsExactly(NotFoundException.class, () -> filmController.update(nonExistingFilm));
    }
}
