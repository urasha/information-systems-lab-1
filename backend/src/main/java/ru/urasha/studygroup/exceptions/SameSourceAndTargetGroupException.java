package ru.urasha.studygroup.exceptions;

public class SameSourceAndTargetGroupException extends StudyGroupException {

    public SameSourceAndTargetGroupException() {
        super(ExceptionMessages.SAME_SOURCE_AND_TARGET.getMessage());
    }
}
