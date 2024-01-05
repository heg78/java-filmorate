package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    Integer id;
    @NotBlank(message = "Название фильма должно быть заполнено")
    String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    String description;
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    Integer duration;
}
