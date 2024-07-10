package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.UserUpdateDTO;
import ru.yandex.practicum.filmorate.model.User;

@UtilityClass
@Slf4j
public class UserMapper {
    public User mapToUser(UserDTO userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .name(userDto.getName())
                .birthday(userDto.getBirthday())
                .build();
        log.info("Преобразование UserDTO в User успешно завершено");
        return user;
    }

    public UserDTO mapToUserDto(User user) {
        UserDTO userDto = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
        log.info("Преобразование User в UserDTO успешно завершено");
        return userDto;
    }

    public User mapToUser(UserUpdateDTO updateUserDto) {
        User user = User.builder()
                .id(updateUserDto.getId())
                .email(updateUserDto.getEmail())
                .login(updateUserDto.getLogin())
                .name(updateUserDto.getName())
                .birthday(updateUserDto.getBirthday())
                .build();
        log.info("Преобразование UserUpdateDTO в User успешно завершено");
        return user;
    }
}
