package ru.urasha.studygroup.controllers.exceptions.handlers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urasha.studygroup.controllers.ImportController;
import ru.urasha.studygroup.dto.ImportErrorsResponseDto;
import ru.urasha.studygroup.dto.ImportErrorDto;
import ru.urasha.studygroup.exceptions.ImportException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice(assignableTypes = ImportController.class)
public class ImportExceptionHandler {

    @ExceptionHandler(ImportException.class)
    public ResponseEntity<ImportErrorsResponseDto> handleImportException(ImportException ex) {
        List<ImportErrorDto> errors = ex.getErrors();
        ImportErrorsResponseDto body = new ImportErrorsResponseDto(
                LocalDateTime.now(),
                "Import failed",
                errors
        );
        return ResponseEntity.badRequest().body(body);
    }
}
