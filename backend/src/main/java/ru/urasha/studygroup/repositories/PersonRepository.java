package ru.urasha.studygroup.repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ru.urasha.studygroup.models.Person;

@Repository
public interface PersonRepository extends ListCrudRepository<Person, Integer> {
}
