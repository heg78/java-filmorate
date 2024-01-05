package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmCreateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    FilmController filmController;
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createEmptyFilmName() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now());
        assertTrue(filmValidatorHasErrorMessage(film, "Название фильма должно быть заполнено"));
    }

    @Test
    void createDescriptionFilmMore200() {
        Film film = new Film();
        film.setDescription("s".repeat(205));
        assertTrue(filmValidatorHasErrorMessage(film, "Максимальная длина описания - 200 символов"));
    }

    @Test
    void createReleaseDateFilmBefore1895() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1894, 12, 28));

        final FilmCreateException exception = assertThrows(
                FilmCreateException.class,
                () -> filmController.create(film));

        assertEquals(FilmCreateException.class, exception.getClass());
        assertEquals(exception.getMessage(), "Дата релиза должна быть не ранее 1895-12-28");
    }

    @Test
    void createNegativeDurationFilm() {
        Film film = new Film();
        film.setDuration(-1);
        assertTrue(filmValidatorHasErrorMessage(film, "Продолжительность фильма должна быть положительной"));
    }

    public static boolean filmValidatorHasErrorMessage(Film film, String message) {
        Set<ConstraintViolation<Film>> errors = VALIDATOR.validate(film);
        return errors.stream().map(ConstraintViolation::getMessage).anyMatch(message::equals);
    }
}
