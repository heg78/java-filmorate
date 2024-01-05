package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmCreateException;
import ru.yandex.practicum.filmorate.exception.UserCreateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 0;

    @GetMapping()
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            throw new UserCreateException("Имя пользователя не может содержать пробелы");
        }
        user.setId(++id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь " + user.getName());
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getLogin().contains(" ")) {
                throw new UserCreateException("Имя пользователя не может содержать пробелы");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь " + user.getName() + " изменен");
        } else {
            throw new UserCreateException("Пользователь с указанным ID не найден");
        }
        return user;
    }
}
