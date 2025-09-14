package ru.urasha.studygroup.repositories;

import ru.urasha.studygroup.models.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {

    Page<StudyGroup> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select distinct studyGroup.groupAdmin from StudyGroup studyGroup")
    List<?> findDistinctGroupAdmins();
}
