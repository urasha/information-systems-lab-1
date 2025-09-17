package ru.urasha.studygroup.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionMessages {

    STUDENT_GROUP_NOT_FOUND("StudentGroup with ID: %d not found");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }
}
