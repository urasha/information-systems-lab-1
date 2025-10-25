package ru.urasha.studygroup.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urasha.studygroup.dto.StudyGroupDto;
import ru.urasha.studygroup.events.StudyGroupChangedEvent;
import ru.urasha.studygroup.exceptions.StudyGroupNotFoundException;
import ru.urasha.studygroup.mappers.StudyGroupMapper;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.repositories.StudyGroupRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupRepository repository;
    private final StudyGroupMapper studyGroupMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final UniqueConstraintService uniqueConstraintService;

    public Page<StudyGroup> getGroupPage(String nameContains, Pageable pageable) {
        return nameContains == null || nameContains.isBlank()
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCase(nameContains, pageable);
    }

    public Optional<StudyGroup> get(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StudyGroup create(StudyGroupDto dto) {
        uniqueConstraintService.checkUniqueForCreate(dto);

        StudyGroup group = studyGroupMapper.toEntity(dto);
        StudyGroup saved = repository.save(group);

        eventPublisher.publishEvent(
                new StudyGroupChangedEvent(saved.getId(), StudyGroupChangedEvent.EventType.CREATED)
        );

        return saved;
    }

    @Transactional
    public StudyGroup update(Integer id, StudyGroupDto updatedGroupDto) throws StudyGroupNotFoundException {
        StudyGroup existingGroup = repository.findById(id)
                .orElseThrow(() -> new StudyGroupNotFoundException(id));

        uniqueConstraintService.checkUniqueForUpdate(id, updatedGroupDto);

        studyGroupMapper.updateEntityFromDto(updatedGroupDto, existingGroup);
        StudyGroup saved = repository.save(existingGroup);

        eventPublisher.publishEvent(
                new StudyGroupChangedEvent(saved.getId(), StudyGroupChangedEvent.EventType.UPDATED)
        );

        return saved;
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);

        eventPublisher.publishEvent(
                new StudyGroupChangedEvent(id, StudyGroupChangedEvent.EventType.DELETED)
        );
    }
}
