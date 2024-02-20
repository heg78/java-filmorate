package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.CreateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate MINFILMDATE = LocalDate.of(1895, 12, 28);

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film> findAll() {
        String sqlQuery = "select f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa, r.name as rname from films f " +
                "left join ref_mpa r on f.mpa=r.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    public Film create(Film film) {
        if (MINFILMDATE.isAfter(film.getReleaseDate())) {
            throw new CreateException("Дата релиза должна быть не ранее 1895-12-28");
        }

        String sqlQuery = "insert into films(name, description, release_date, duration, mpa) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            //stmt.setInt(5, film.getRate());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        updateGenre(film);
        //films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.getName());
        return film;
    }

    public Film update(Film film) {
        if (!exists(film.getId())) throw new NotFoundException("Фильм с указанным Id не найден");

        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "where id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            //stmt.setInt(5, film.getRate());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setInt(6, film.getId());
            return stmt;
        });

        log.info("Фильм " + film.getName() + " изменен");
        return updateGenre(film);
    }

    public Film find(Integer id) {
        if (!exists(id)) throw new NotFoundException("Фильм с указанным Id не найден");
        String sqlQuery = "select f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa, r.name as rname from films f " +
                "left join ref_mpa r on f.mpa=r.id where f.id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    private boolean exists(Integer id) {
        String sqlQuery = "select exists(select 1 from films where id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id));
    }

    public void addLike(Integer filmId, Integer userID) {
        if (!exists(filmId)) throw new NotFoundException("Фильм с указанным Id не найден");
        String sqlQuery = "insert into likes(film_id, user_id) values(?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            stmt.setInt(2, userID);
            return stmt;
        });
    }

    public void deleteLike(Integer filmId, Integer userID) {
        if (!exists(filmId)) throw new NotFoundException("Фильм с указанным Id не найден");
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            stmt.setInt(2, userID);
            return stmt;
        });
    }

    private void clearGenre(Integer filmId) {
        if (!exists(filmId)) throw new NotFoundException("Фильм с указанным Id не найден");
        String sqlQuery = "delete from genres where film_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            return stmt;
        });
    }

    private Film updateGenre(Film film) {
        if (!exists(film.getId())) throw new NotFoundException("Фильм с указанным Id не найден");

        clearGenre(film.getId());

        List<Genre> newGenres = Optional.ofNullable(film.getGenres()).orElse(new ArrayList<>());
        if (newGenres.isEmpty()) return film;

        Set<Genre> newFilteredGenres = new HashSet<>(newGenres);
        List<Genre> filterGenre = newFilteredGenres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());

        film.setGenres(filterGenre);

        String sqlQuery = "insert into genres(film_id, genre_id) values(?, ?);";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement stmt, int i) throws SQLException {
                stmt.setInt(1, film.getId());
                stmt.setInt(2, filterGenre.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return filterGenre.size();
            }
        });
        return film;
    }

    @Override
    public List<Integer> getLikes(Integer filmId) {
        if (!exists(filmId)) throw new NotFoundException("Фильм с указанным Id не найден");
        String sqlQuery = "select user_id from likes where film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
    }

    private List<Genre> getGenres(Integer filmId) {
        String sqlQuery = "select g.genre_id, r.name from genres g left join ref_genres r on r.id=g.genre_id where g.film_id = ? order by g.genre_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRefGenre, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(Mpa.builder().id(resultSet.getInt("mpa")).name(resultSet.getString("rname")).build())
                .genres(getGenres(resultSet.getInt("id")))
                .build();
    }

    private Genre mapRowToRefGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
