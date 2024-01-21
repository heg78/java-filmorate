package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.CreateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 0;

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getLogin().contains(" ")) {
            throw new CreateException("Имя пользователя не может содержать пробелы");
        }
        user.setId(++id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user.getName());
        return user;
    }

    public User update(User user) {
        if (users.containsKey(user.getId())) {
            if (user.getLogin().contains(" ")) {
                throw new CreateException("Имя пользователя не может содержать пробелы");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь {} изменен", user.getName() );
        } else {
            throw new CreateException("Пользователь с указанным ID не найден");
        }
        return user;
    }

    public User find(Integer id) {
        return users.get(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        users.get(userId).getFriends().add(friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        users.get(userId).getFriends().remove(friendId);
    }
}
