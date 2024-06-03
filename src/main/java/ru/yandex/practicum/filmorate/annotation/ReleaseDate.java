package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface ReleaseDate {
    String message() default "Дата релиза должна быть после 28 декабря 1895 г.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
