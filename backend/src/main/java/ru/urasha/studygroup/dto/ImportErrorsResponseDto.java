package ru.urasha.studygroup.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ImportErrorsResponseDto(
        LocalDateTime timestamp,
        String message,
        List<ImportErrorDto> errors
) {
}
