package ru.urasha.studygroup.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urasha.studygroup.events.StudyGroupChangedEvent;
import ru.urasha.studygroup.exceptions.SameSourceAndTargetGroupException;
import ru.urasha.studygroup.exceptions.StudyGroupNotFoundException;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.repositories.StudyGroupRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecialOpsService {

    private final StudyGroupRepository studyGroupRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void deleteByAdminName(String adminName) {
        List<StudyGroup> groupsWithAdmin = studyGroupRepository.findByGroupAdmin_Name(adminName);

        if (groupsWithAdmin.isEmpty()) {
            return;
        }

        for (StudyGroup group : groupsWithAdmin) {
            Integer groupId = group.getId();
            studyGroupRepository.delete(group);
            eventPublisher.publishEvent(
                    new StudyGroupChangedEvent(groupId, StudyGroupChangedEvent.EventType.DELETED)
            );
        }
    }

    @Transactional(readOnly = true)
    public List<StudyGroup> searchByName(String substring) {
        return studyGroupRepository.findByNameContainingIgnoreCase(substring);
    }

    @Transactional(readOnly = true)
    public List<String> getUniqueAdminNames() {
        return studyGroupRepository.findDistinctGroupAdminNames();
    }

    @Transactional
    public StudyGroup expelAllStudents(Integer groupId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(groupId));

        long expelledCount = group.getStudentsCount();
        group.setExpelledStudents(Math.max(group.getExpelledStudents(), 0) + expelledCount);
        group.setStudentsCount(0);

        StudyGroup savedGroup = studyGroupRepository.save(group);

        eventPublisher.publishEvent(
                new StudyGroupChangedEvent(savedGroup.getId(), StudyGroupChangedEvent.EventType.UPDATED)
        );

        return savedGroup;
    }

    @Transactional
    public Map<String, StudyGroup> transferAllStudents(Integer fromGroupId, Integer toGroupId) {
        if (fromGroupId.equals(toGroupId)) {
            throw new SameSourceAndTargetGroupException();
        }

        StudyGroup sourceGroup = studyGroupRepository.findById(fromGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(fromGroupId));

        StudyGroup targetGroup = studyGroupRepository.findById(toGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(toGroupId));

        int studentsToTransfer = sourceGroup.getStudentsCount();

        targetGroup.setStudentsCount(targetGroup.getStudentsCount() + studentsToTransfer);
        sourceGroup.setTransferredStudents(sourceGroup.getTransferredStudents() + studentsToTransfer);
        sourceGroup.setStudentsCount(0);

        StudyGroup savedSourceGroup = studyGroupRepository.save(sourceGroup);
        StudyGroup savedTargetGroup = studyGroupRepository.save(targetGroup);

        eventPublisher.publishEvent(
                new StudyGroupChangedEvent(savedSourceGroup.getId(), StudyGroupChangedEvent.EventType.UPDATED)
        );
        eventPublisher.publishEvent(
                new StudyGroupChangedEvent(savedTargetGroup.getId(), StudyGroupChangedEvent.EventType.UPDATED)
        );

        return Map.of("from", savedSourceGroup, "to", savedTargetGroup);
    }
}
