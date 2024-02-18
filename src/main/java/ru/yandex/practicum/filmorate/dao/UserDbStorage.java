package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.CreateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    //private final Map<Integer, User> users = new HashMap<>();
    //private static int id = 0;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<User> findAll() {
        String sqlQuery = "select id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    public User create(User user) {
        if (user.getLogin().contains(" ")) {
            throw new CreateException("Имя пользователя не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        String sqlQuery = "insert into users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Добавлен пользователь {}", user.getName());
        return user;
    }

    public User update(User user) {
        if (exists(user.getId())) {
            if (user.getLogin().contains(" ")) {
                throw new CreateException("Имя пользователя не может содержать пробелы");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }

            String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? " +
                    "where id = ?";
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                stmt.setInt(5, user.getId());
                return stmt;
            });
            log.info("Пользователь {} изменен", user.getName());
            return user;
        } else {
            throw new NotFoundException("Пользователь с указанным ID не найден");
        }
    }

    public User find(Integer id) {
        if (exists(id)) {
            String sqlQuery = "select id, email, login, name, birthday " +
                    "from users where id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } else {
            throw new NotFoundException("Пользователь с указанным ID не найден");
        }
    }

    public boolean exists(Integer id) {
        String sqlQuery = "select exists(select 1 from users where id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id));
    }

    public void addFriend(Integer userId, Integer friendId) {
        String sqlQuery = "insert into frends(user_id, frend_id) values(?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            return stmt;
        });
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlQuery = "delete from frends where user_id = ? and frend_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            return stmt;
        });
    }

    @Override
    public List<Integer> getFriends(Integer userId) {
        if (!exists(userId)) throw new NotFoundException("Пользователь с указанным ID не найден");
        String sqlQuery = "select frend_id from frends where user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
