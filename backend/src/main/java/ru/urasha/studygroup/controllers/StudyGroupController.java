package ru.urasha.studygroup.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urasha.studygroup.dto.StudyGroupDto;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.services.StudyGroupService;


@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService service;

    @GetMapping
    public Page<StudyGroup> list(
            @RequestParam(required = false) String nameContains,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") boolean asc
    ) {
        Sort.Direction dir = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable p = PageRequest.of(page, size, Sort.by(dir, sort));
        return service.list(nameContains, p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyGroup> get(@PathVariable Integer id) {
        return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudyGroup> create(@Valid @RequestBody StudyGroupDto dto) {
        StudyGroup saved = service.create(dto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyGroup> update(@PathVariable Integer id,
                                             @Valid @RequestBody StudyGroup group) {
        try {
            StudyGroup updated = service.update(id, group);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
