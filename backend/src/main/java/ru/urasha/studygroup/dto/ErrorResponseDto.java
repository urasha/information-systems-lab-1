package ru.urasha.studygroup.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(LocalDateTime timestamp, String message) {
}
