package ru.urasha.studygroup.repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ru.urasha.studygroup.models.Coordinates;

@Repository
public interface CoordinatesRepository extends ListCrudRepository<Coordinates, Integer> {
}
