package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundData(NotFoundException e) {
        log.error("Возникло исключение NotFoundException. " + e.getMessage());
        return new ErrorResponse("Данные не найдены", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidationData(ValidationException e) {
        log.error("Возникло исключение ValidationException. " + e.getMessage());
        return new ErrorResponse("Введены некорректные данные", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(InternalServerException e) {
        log.error("Возникло исключение InternalServerException. " + e.getMessage());
        return new ErrorResponse("Ошибка взаимодействия с БД", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNotValidationData(Throwable e) {
        log.error("Возникло исключение Throwable. " + e.getMessage());
        return new ErrorResponse("Ошибка", "Произошла непредвиденная ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidationData1(MethodArgumentNotValidException e) {
        log.error("Возникло исключение MethodArgumentNotValidException. " + e.getMessage());
        return new ErrorResponse("Введены некорректные данные", e.getMessage());
    }
}
