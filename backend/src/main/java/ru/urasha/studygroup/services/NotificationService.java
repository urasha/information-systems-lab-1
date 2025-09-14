package ru.urasha.studygroup.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate template;

    public void broadcast(Object payload) {
        template.convertAndSend("/topic/groups", payload);
    }
}
