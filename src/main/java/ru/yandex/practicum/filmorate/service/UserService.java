package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User find(Integer id) {
        User user = userStorage.find(id);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userStorage.find(userId) != null && userStorage.find(friendId) != null) {
            userStorage.addFriend(userId, friendId);
            userStorage.addFriend(friendId, userId);
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        userStorage.deleteFriend(friendId, userId);
    }

    public List<User> getFriend(Integer id) {
        if (userStorage.find(id) != null) {
            return userStorage.find(id).getFriends().stream()
                    .map(u -> userStorage.find(u))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public List<User> commonFriend(Integer id, Integer otherId) {
        return userStorage.find(id).getFriends().stream()
                .filter(u -> userStorage.find(otherId).getFriends().contains(u))
                .map(u -> userStorage.find(u))
                .collect(Collectors.toList());
    }
}
