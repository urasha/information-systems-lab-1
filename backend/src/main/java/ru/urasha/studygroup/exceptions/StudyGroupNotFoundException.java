package ru.urasha.studygroup.exceptions;

public class StudyGroupNotFoundException extends RuntimeException {
    public StudyGroupNotFoundException(String message) {
        super(message);
    }
}
