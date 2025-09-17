package ru.urasha.studygroup.events;

public record StudyGroupChangedEvent(Integer id, EventType type) {

    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}
