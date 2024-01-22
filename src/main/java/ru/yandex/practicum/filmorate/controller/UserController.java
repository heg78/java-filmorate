package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping()
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User find(@PathVariable("id") Integer id) {
        return userService.find(id);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriend(@PathVariable("id") Integer id) {
        return userService.getFriend(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> commonFriend(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        return userService.commonFriend(id, otherId);
    }
}
