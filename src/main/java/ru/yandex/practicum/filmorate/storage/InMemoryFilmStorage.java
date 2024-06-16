package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmDTO;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, FilmDTO> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public void addFilm(Long id, FilmDTO film) {
        films.put(id, film);
        likes.put(id, new HashSet<>());
    }

    @Override
    public FilmDTO getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public List<FilmDTO> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Long> getFilmsIds() {
        return new ArrayList<>(films.keySet());
    }

    @Override
    public Boolean containsFilm(Long id) {
        return films.containsKey(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        likes.get(filmId).add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        likes.get(filmId).remove(userId);
    }

    @Override
    public Map<Long, Set<Long>> getFilmsLikes() {
        return likes;
    }
}
