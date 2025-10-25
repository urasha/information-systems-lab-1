package ru.urasha.studygroup.controllers.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urasha.studygroup.controllers.StudyGroupController;
import ru.urasha.studygroup.dto.DefaultErrorResponseDto;
import ru.urasha.studygroup.dto.ImportErrorDto;
import ru.urasha.studygroup.exceptions.StudyGroupException;
import ru.urasha.studygroup.exceptions.UniqueConstraintException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(assignableTypes = StudyGroupController.class)
public class StudyGroupExceptionHandler {

    @ExceptionHandler(StudyGroupException.class)
    public ResponseEntity<DefaultErrorResponseDto> handleStudyGroupException(StudyGroupException exception) {
        DefaultErrorResponseDto body = new DefaultErrorResponseDto(
                LocalDateTime.now(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UniqueConstraintException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessValidation(UniqueConstraintException exception) {
        List<ImportErrorDto> errors = exception.getErrors();
        Map<String,Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "message", "Validation failed",
                "errors", errors
        );
        return ResponseEntity.badRequest().body(body);
    }
}
