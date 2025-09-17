package ru.urasha.studygroup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urasha.studygroup.exceptions.StudyGroupNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = StudyGroupController.class)
public class StudyGroupExceptionHandler {

    @ExceptionHandler(StudyGroupNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(StudyGroupNotFoundException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
