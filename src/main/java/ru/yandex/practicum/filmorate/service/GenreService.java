package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

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
        Optional<Genre> genreOptional = genreRepository.getGenreById(id);
        if (genreOptional.isEmpty()) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
        return GenreMapper.mapToGenreDto(genreOptional.get());
    }

}
