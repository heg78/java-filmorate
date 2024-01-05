package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmCreateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private static int id = 0;
    private static final LocalDate MINFILMDATE = LocalDate.of(1895, 12, 28);

    @GetMapping()
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        film.setId(++id);
        if (MINFILMDATE.isAfter(film.getReleaseDate())) {
            throw new FilmCreateException("Дата релиза должна быть не ранее 1895-12-28");
        }
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.getName());
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new FilmCreateException("Фильм с указанным Id не найден");
        }
        log.info("Фильм " + film.getName() + " изменен");
        return film;
    }
}
