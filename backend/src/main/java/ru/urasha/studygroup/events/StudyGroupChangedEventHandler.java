package ru.urasha.studygroup.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.urasha.studygroup.services.NotificationService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StudyGroupChangedEventHandler {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StudyGroupChangedEvent event) {
        Map<String, Object> payload = Map.of(
                "event", event.type().name().toLowerCase(),
                "id", event.id()
        );
        notificationService.broadcast(payload);
    }
}
