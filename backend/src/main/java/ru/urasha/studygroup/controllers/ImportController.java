package ru.urasha.studygroup.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.urasha.studygroup.dto.ImportResultDto;
import ru.urasha.studygroup.services.ImportService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping("/import")
    public ResponseEntity<ImportResultDto> importFile(@RequestParam("file") MultipartFile file) {
        ImportResultDto result = importService.importFromFile(file);
        return ResponseEntity.ok(result);
    }
}
