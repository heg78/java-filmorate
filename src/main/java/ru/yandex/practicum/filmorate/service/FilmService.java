package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film find(Integer id) {
        Film film = filmStorage.find(id);
        if (film != null) {
            return film;
        } else {
            throw new NotFoundException("Фильм не найден!");
        }
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> filmStorage.getLikes(f2.getId()).size() - filmStorage.getLikes(f1.getId()).size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void addLike(Integer id, Integer userId) {
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        List<Integer> likes = filmStorage.getLikes(id);
        if (!likes.contains(userId)) {
            throw new NotFoundException("Лайк пользователя не найден!");
        } else {
            filmStorage.deleteLike(id, userId);
        }
    }
}
