package ru.urasha.studygroup.exceptions;

public class StudyGroupNotFoundException extends StudyGroupException {

    public StudyGroupNotFoundException(Integer id) {
        super(String.format(ExceptionMessages.STUDY_GROUP_NOT_FOUND.getMessage(), id));
    }
}
