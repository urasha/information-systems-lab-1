package ru.urasha.studygroup.controllers.exceptions.handlers.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urasha.studygroup.dto.ErrorResponseDto;
import ru.urasha.studygroup.exceptions.StudyGroupException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudyGroupException.class)
    public ResponseEntity<ErrorResponseDto> handleStudyGroupException(StudyGroupException exception) {
        ErrorResponseDto body = new ErrorResponseDto(
                LocalDateTime.now(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAnyException(Exception exception) {
        ErrorResponseDto body = new ErrorResponseDto(
                LocalDateTime.now(),
                "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
