package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

@UtilityClass
@Slf4j
public class GenreMapper {
    public Genre mapToGenre(GenreDTO genreDto) {
        Genre genre = Genre.builder()
                .id(genreDto.getId())
                .build();
        log.info("Преобразование GenreDTO в Genre успешно завершено");
        return genre;
    }

    public GenreDTO mapToGenreDto(Genre genre) {
        GenreDTO genreDto = GenreDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
        log.info("Преобразование Genre в GenreDTO успешно завершено");
        return genreDto;
    }
}
