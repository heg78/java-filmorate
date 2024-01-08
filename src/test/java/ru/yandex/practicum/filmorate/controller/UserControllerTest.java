package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.CreateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    UserController userController;
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createNotValidEmail() {
        User user = new User();
        assertTrue(userValidatorHasErrorMessage(user, "Email не может быть пустым"));
        user.setEmail("ItIsNotEmail");
        assertTrue(userValidatorHasErrorMessage(user, "Формат не соответствует Email"));
    }

    @Test
    void createEmptyLogin() {
        User user = new User();
        assertTrue(userValidatorHasErrorMessage(user, "Логин не может быть пустым"));
    }

    @Test
    void createLoginWithWhiteSpace() {
        User user = new User();
        user.setLogin("Lo gin");
        final CreateException exception = assertThrows(
                CreateException.class,
                () -> userController.create(user));

        assertEquals(CreateException.class, exception.getClass());
        assertEquals(exception.getMessage(), "Имя пользователя не может содержать пробелы");
    }

    @Test
    void createEmptyName() {
        User user = new User();
        user.setLogin("Login");
        assertEquals(user.getLogin(), userController.create(user).getName());
    }

    @Test
    void createFutureBirthDay() {
        User user = new User();
        user.setBirthday(LocalDate.of(2025, 12, 28));
        assertTrue(userValidatorHasErrorMessage(user, "Дата рождения не может быть в будущем"));
    }

    public static boolean userValidatorHasErrorMessage(User user, String message) {
        Set<ConstraintViolation<User>> errors = VALIDATOR.validate(user);
        return errors.stream().map(ConstraintViolation::getMessage).anyMatch(message::equals);
    }
}
