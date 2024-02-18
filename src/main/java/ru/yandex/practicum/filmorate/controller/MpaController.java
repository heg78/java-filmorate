package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController()
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    @Autowired
    private final MpaService mpaService;

    @GetMapping()
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa find(@PathVariable("id") Integer id) {
        return mpaService.find(id);
    }
}
