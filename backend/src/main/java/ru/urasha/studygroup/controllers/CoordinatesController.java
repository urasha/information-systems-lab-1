package ru.urasha.studygroup.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urasha.studygroup.models.Coordinates;
import ru.urasha.studygroup.repositories.CoordinatesRepository;

import java.util.List;

@RestController
@RequestMapping("/api/coordinates")
@RequiredArgsConstructor
public class CoordinatesController {

    private final CoordinatesRepository coordinatesRepository;

    @GetMapping
    public ResponseEntity<List<Coordinates>> listAll() {
        return ResponseEntity.ok().body(coordinatesRepository.findAll());
    }
}
