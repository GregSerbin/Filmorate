package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class FilmMapper {
    public Film mapToFilm(FilmDTO filmDto) {
        List<GenreDTO> requestGenreDto = filmDto.getGenres();
        List<Genre> genres = new ArrayList<>();

        if (requestGenreDto != null) {
            genres = requestGenreDto.stream()
                    .map(GenreMapper::mapToGenre)
                    .toList();
        }
        Film film = Film.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .releaseDate(filmDto.getReleaseDate())
                .duration(filmDto.getDuration())
                .genres(genres)
                .mpa(MpaMapper.mapToMpa(filmDto.getMpa()))
                .build();
        log.info("Преобразование FilmDTO в Film успешно завершено");
        return film;
    }

    public Film mapToFilm(FilmUpdateDTO updateFilmDto) {
        List<GenreDTO> requestGenreDto = updateFilmDto.getGenres();
        List<Genre> genres = new ArrayList<>();

        if (requestGenreDto != null) {
            genres = requestGenreDto.stream()
                    .map(GenreMapper::mapToGenre)
                    .toList();
        }
        Film film = Film.builder()
                .id(updateFilmDto.getId())
                .name(updateFilmDto.getName())
                .description(updateFilmDto.getDescription())
                .releaseDate(updateFilmDto.getReleaseDate())
                .duration(updateFilmDto.getDuration())
                .genres(genres)
                .mpa(MpaMapper.mapToMpa(updateFilmDto.getMpa()))
                .build();
        log.info("Преобразование FilmUpdateDTO в Film успешно завершено");
        return film;
    }

    public FilmDTO mapToFilmDto(Film film) {
        log.info("Начало преобразования Film в FilmDTO");
        List<Genre> genres = film.getGenres();
        List<GenreDTO> responseGenreDto = new ArrayList<>();

        if (genres != null) {
            responseGenreDto = genres.stream()
                    .map(GenreMapper::mapToGenreDto)
                    .collect(Collectors.toList());
        }

        FilmDTO filmDto = FilmDTO.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .genres(responseGenreDto)
                .mpa(MpaMapper.mapToMpaDto(film.getMpa()))
                .build();
        log.info("Преобразование Film в FilmDTO успешно завершено");
        return filmDto;
    }

    public FilmDTO mapToFilmDto(FilmUpdateDTO filmUpdateDTO) {
        log.info("Начало преобразования FilmUpdateDTO в FilmDTO");
        List<GenreDTO> genres = filmUpdateDTO.getGenres();

        FilmDTO filmDto = FilmDTO.builder()
                .id(filmUpdateDTO.getId())
                .name(filmUpdateDTO.getName())
                .description(filmUpdateDTO.getDescription())
                .releaseDate(filmUpdateDTO.getReleaseDate())
                .duration(filmUpdateDTO.getDuration())
                .genres(genres)
                .mpa(filmUpdateDTO.getMpa())
                .build();
        log.info("Преобразование FilmUpdateDTO в FilmDTO успешно завершено");
        return filmDto;
    }

}
