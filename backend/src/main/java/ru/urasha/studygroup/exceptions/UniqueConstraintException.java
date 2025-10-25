package ru.urasha.studygroup.exceptions;

import lombok.Getter;
import ru.urasha.studygroup.dto.ImportErrorDto;

import java.util.List;

@Getter
public class UniqueConstraintException extends RuntimeException {

    private final List<ImportErrorDto> errors;

    public UniqueConstraintException(List<ImportErrorDto> errors) {
        super("Unique constraints validation failed");
        this.errors = errors;
    }
}