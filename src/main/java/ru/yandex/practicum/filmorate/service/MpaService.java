package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public List<MpaDTO> getAllMpaRatings() {
        log.info("Получен запрос на получение всех рейтингов");
        List<Mpa> mpa = mpaRepository.getAllMpaRatings();
        return mpa.stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();
    }

    public MpaDTO getMpaRatingById(int id) {
        log.info("Получен запрос на получение mpa рейтинга с id={}", id);
        Mpa mpa = mpaRepository.getMpaRating(id);
        return MpaMapper.mapToMpaDto(mpa);
    }
}
