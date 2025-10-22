package ru.urasha.studygroup.exceptions;

import lombok.Getter;
import ru.urasha.studygroup.dto.ImportErrorDto;

import java.util.List;

@Getter
public class ImportException extends RuntimeException {
    private final List<ImportErrorDto> errors;

    public ImportException(List<ImportErrorDto> errors) {
        super("Import failed: " + (errors == null ? 0 : errors.size()) + " error(s)");
        this.errors = errors;
    }

}
