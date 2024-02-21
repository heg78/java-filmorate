package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
    private final User newUser2 = new User(2, "user2@email.ru", "vanya1232", "Ivan Ivanov", LocalDate.of(1991, 10, 10));

    @Test
    public void testFindUserById() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Integer userId = userStorage.create(newUser).getId();

        User savedUser = userStorage.find(userId);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testFindAllUsers() {
        List<User> users = List.of(newUser, newUser2);

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);
        userStorage.create(newUser2);

        Collection<User> savedUsers = userStorage.findAll();

        assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(users);
    }

    @Test
    public void testUpdateUser() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Integer userID = userStorage.create(newUser).getId();
        newUser2.setId(userID);
        userStorage.update(newUser2);

        User updatedUser = userStorage.find(userID);

        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser2);
    }

    @Test
    public void testAddAndDeleteFriend() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Integer userID = userStorage.create(newUser).getId();
        Integer userID2 = userStorage.create(newUser2).getId();
        userStorage.addFriend(userID, userID2);
        assertThat(userStorage.getFriends(userID))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(userID2));

        userStorage.deleteFriend(userID, userID2);
        assertThat(userStorage.getFriends(userID))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of());
    }
}
