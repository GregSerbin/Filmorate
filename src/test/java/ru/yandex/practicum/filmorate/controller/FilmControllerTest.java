package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmUpdateDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private FilmDTO defaultFilm;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        defaultFilm = FilmDTO.builder()
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
    public void updateFilmShouldNotThrowExceptionWhenFilmIsValid() {
        FilmDTO createdFilm = filmController.create(defaultFilm);
        FilmUpdateDTO updatedFilm = FilmUpdateDTO.builder()
                .id(createdFilm.getId())
                .name("Updated Name")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .build();
        assertDoesNotThrow(() -> filmController.update(updatedFilm));
    }

    @Test
    public void getAllFilmsShouldReturnAllFilms() {
        filmController.create(defaultFilm);
        assertEquals(1, filmController.findAll().size(), "Количество добавленных и полученных фильмов должно совпадать");
    }

    @Test
    public void updateShouldThrowExceptionIfUpdateOfNonExistingFilm() {
        long idOfNonExistingFilm = 100500;
        FilmUpdateDTO nonExistingFilm = FilmUpdateDTO.builder()
                .id(idOfNonExistingFilm)
                .build();

        assertThrowsExactly(NotFoundException.class, () -> filmController.update(nonExistingFilm));
    }
}
