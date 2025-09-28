package ru.urasha.studygroup.dto;

import java.time.LocalDateTime;

public record DefaultErrorResponseDto(LocalDateTime timestamp, String message) {
}
