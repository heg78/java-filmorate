package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film find(Integer id);

    List<Integer> getLikes(Integer filmId);

    void addLike(Integer filmId, Integer userID);

    void deleteLike(Integer filmId, Integer userID);
}
