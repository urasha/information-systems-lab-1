package ru.urasha.studygroup.repositories;

import ru.urasha.studygroup.models.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {

    Page<StudyGroup> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<StudyGroup> findByGroupAdmin_Name(String name);

    List<StudyGroup> findByNameContainingIgnoreCase(String substring);

    @Query("select distinct g.groupAdmin.name from StudyGroup g where g.groupAdmin.name is not null")
    List<String> findDistinctGroupAdminNames();
}
