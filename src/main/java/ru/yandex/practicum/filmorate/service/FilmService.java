package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    FilmStorage filmStorage;


    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

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
        if (filmStorage.find(id) != null) {
            return filmStorage.find(id);
        } else {
            throw new NotFoundException("Фильм не найден!");
        }
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void addLike(Integer id, Integer userId) {
        filmStorage.find(id).getLikes().add(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (!filmStorage.find(id).getLikes().contains(userId)) {
            throw new NotFoundException("Пользователь не найден!");
        } else {
            filmStorage.find(id).getLikes().remove(userId);
        }
    }
}
