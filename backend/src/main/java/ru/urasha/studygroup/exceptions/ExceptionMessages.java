package ru.urasha.studygroup.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionMessages {

    STUDY_GROUP_NOT_FOUND("StudyGroup with ID: %d not found"),
    SAME_SOURCE_AND_TARGET("Source and target groups must be different");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }
}
