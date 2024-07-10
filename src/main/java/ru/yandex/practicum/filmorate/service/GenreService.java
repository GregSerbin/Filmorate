package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<GenreDTO> getAllGenres() {
        log.info("Получен запрос на получение всех жанров");
        List<Genre> genres = genreRepository.getAllGenres();
        return genres.stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDTO getGenreById(int id) {
        log.info("Получен запрос на получение жанра с id={}", id);
        Genre genre = genreRepository.getGenreById(id);
        return GenreMapper.mapToGenreDto(genre);
    }
}
