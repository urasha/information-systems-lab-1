package ru.urasha.studygroup.controllers.exceptions.handlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urasha.studygroup.controllers.SpecialOpsController;
import ru.urasha.studygroup.dto.ErrorResponseDto;
import ru.urasha.studygroup.exceptions.SameSourceAndTargetGroupException;
import ru.urasha.studygroup.exceptions.StudyGroupNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = SpecialOpsController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpecialOpsExceptionHandler {

    @ExceptionHandler(SameSourceAndTargetGroupException.class)
    public ResponseEntity<ErrorResponseDto> handleSameSourceTarget(SameSourceAndTargetGroupException exception) {
        ErrorResponseDto body = new ErrorResponseDto(
                LocalDateTime.now(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(StudyGroupNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(StudyGroupNotFoundException exception) {
        ErrorResponseDto body = new ErrorResponseDto(
                LocalDateTime.now(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
