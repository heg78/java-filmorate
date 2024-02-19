package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private final Mpa mpa = new Mpa(1, "G");
    private final List<Genre> genres = List.of(new Genre(4, "Триллер"));
    private final User user = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));

    @Test
    public void testFindFilmById() {
        Film newFilm = new Film(1, "Terminator 4", "I’ll Be Back.", LocalDate.of(1990, 1, 1), 100, 0, mpa, genres);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Integer filmId = filmStorage.create(newFilm).getId();

        assertThat(filmStorage.find(filmId))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = new Film(1, "Terminator 4", "I’ll Be Back.", LocalDate.of(1990, 1, 1), 100, 0, mpa, genres);
        Film updatedFilm = new Film(1, "Terminator 5", "I'm  Back.", LocalDate.of(2001, 1, 1), 100, 0, mpa, genres);

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Integer filmId = filmStorage.create(newFilm).getId();
        assertThat(filmStorage.find(filmId))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);

        updatedFilm.setId(filmId);
        filmStorage.update(updatedFilm);
        assertThat(filmStorage.find(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);
    }

    @Test
    public void testAddAndDeleteLike() {
        Film newFilm = new Film(1, "Terminator 4", "I’ll Be Back.", LocalDate.of(1990, 1, 1), 100, 0, mpa, genres);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Integer userId = userStorage.create(user).getId();
        Integer filmId = filmStorage.create(newFilm).getId();

        filmStorage.addLike(filmId, userId);
        assertThat(filmStorage.getLikes(filmId))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(userId));

        filmStorage.deleteLike(filmId,userId);
        assertThat(filmStorage.getLikes(filmId))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of());
    }
}
