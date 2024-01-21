package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CreateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private static int id = 0;
    private static final LocalDate MINFILMDATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        if (MINFILMDATE.isAfter(film.getReleaseDate())) {
            throw new CreateException("Дата релиза должна быть не ранее 1895-12-28");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.getName());
        return film;
    }

    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new CreateException("Фильм с указанным Id не найден");
        }
        log.info("Фильм " + film.getName() + " изменен");
        return film;
    }

    public Film find(Integer id) {
        return films.get(id);
    }
}
