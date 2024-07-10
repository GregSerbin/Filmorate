package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<MpaDTO> getAllMpa() {
        log.info("Получен запрос на получение всех рейтингов");
        return mpaService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaDTO getMpaById(@PathVariable int id) {
        log.info("Получен запрос на получение рейтинга с id={}", id);
        return mpaService.getMpaRatingById(id);
    }
}
