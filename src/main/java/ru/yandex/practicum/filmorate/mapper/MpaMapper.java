package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.Mpa;

@UtilityClass
@Slf4j
public class MpaMapper {
    public Mpa mapToMpa(MpaDTO mpaDto) {
        Mpa mpa = Mpa.builder()
                .id(mpaDto.getId())
                .build();
        log.info("Преобразование MpaDto в Mpa успешно завершено");
        return mpa;
    }

    public MpaDTO mapToMpaDto(Mpa mpa) {
        MpaDTO mpaDto = MpaDTO.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
        log.info("Преобразование Mpa в MpaDto успешно завершено");
        return mpaDto;
    }
}
