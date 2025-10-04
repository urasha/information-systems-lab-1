package ru.urasha.studygroup.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.urasha.studygroup.models.Person;
import ru.urasha.studygroup.repositories.PersonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getAll() {
        return personRepository.findAll();
    }
}
