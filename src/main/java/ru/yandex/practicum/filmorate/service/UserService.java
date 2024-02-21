package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

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
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        userStorage.deleteFriend(friendId, userId);
    }

    public List<User> getFriend(Integer id) {
        return userStorage.getFriends(id).stream()
                .map(userStorage::find)
                .collect(Collectors.toList());
    }

    public List<User> commonFriend(Integer id, Integer otherId) {
        return userStorage.getFriends(id).stream()
                .filter(u -> userStorage.getFriends(otherId).contains(u))
                .map(userStorage::find)
                .collect(Collectors.toList());
    }
}
