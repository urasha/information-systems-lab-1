package ru.urasha.studygroup.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urasha.studygroup.dto.StudyGroupDto;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.repositories.StudyGroupRepository;

import java.util.Optional;

@Service
public class StudyGroupService {

    private final StudyGroupRepository repository;
    private final NotificationService notifier;

    public StudyGroupService(StudyGroupRepository repository, NotificationService notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    public Page<StudyGroup> list(String nameContains, Pageable pageable) {
        if (nameContains == null || nameContains.isBlank()) {
            return repository.findAll(pageable);
        }

        return repository.findByNameContainingIgnoreCase(nameContains, pageable);
    }

    public Optional<StudyGroup> get(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StudyGroup create(StudyGroupDto dto) {
        StudyGroup group = new StudyGroup();
        group.setName(dto.getName());
        group.setCoordinates(dto.getCoordinates());
        group.setStudentsCount(dto.getStudentsCount());
        group.setExpelledStudents(dto.getExpelledStudents());
        group.setTransferredStudents(dto.getTransferredStudents());
        group.setFormOfEducation(dto.getFormOfEducation());
        group.setShouldBeExpelled(dto.getShouldBeExpelled());
        group.setAverageMark(dto.getAverageMark());
        group.setSemesterEnum(dto.getSemesterEnum());
        group.setGroupAdmin(dto.getGroupAdmin());

        StudyGroup saved = repository.save(group);
        notifier.broadcast(saved);
        return saved;
    }

    @Transactional
    public StudyGroup update(Integer id, StudyGroup updated) {
        StudyGroup g = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));

        g.setName(updated.getName());
        g.setAverageMark(updated.getAverageMark());
        g.setStudentsCount(updated.getStudentsCount());
        g.setExpelledStudents(updated.getExpelledStudents());
        g.setTransferredStudents(updated.getTransferredStudents());
        g.setFormOfEducation(updated.getFormOfEducation());
        g.setShouldBeExpelled(updated.getShouldBeExpelled());
        g.setSemesterEnum(updated.getSemesterEnum());
        g.setCoordinates(updated.getCoordinates());
        g.setGroupAdmin(updated.getGroupAdmin());

        StudyGroup saved = repository.save(g);
        notifier.broadcast(saved);
        return saved;
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
        notifier.broadcast("deleted:" + id);
    }
}
