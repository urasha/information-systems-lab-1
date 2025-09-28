package ru.urasha.studygroup.controllers.exceptions.handlers.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urasha.studygroup.dto.DefaultErrorResponseDto;
import ru.urasha.studygroup.exceptions.StudyGroupException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudyGroupException.class)
    public ResponseEntity<DefaultErrorResponseDto> handleStudyGroupException(StudyGroupException exception) {
        DefaultErrorResponseDto body = new DefaultErrorResponseDto(
                LocalDateTime.now(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        });
        Map<String, Object> body = Map.of(
                "message", "Validation failed",
                "fieldErrors", fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorResponseDto> handleAnyException(Exception exception) {
        DefaultErrorResponseDto body = new DefaultErrorResponseDto(
                LocalDateTime.now(),
                "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
