package ru.urasha.studygroup.mappers;

import org.mapstruct.*;
import ru.urasha.studygroup.dto.StudyGroupDto;
import ru.urasha.studygroup.models.StudyGroup;

@Mapper(componentModel = "spring")
public interface StudyGroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    StudyGroup toEntity(StudyGroupDto dto);

    StudyGroupDto toDto(StudyGroup entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    void updateEntityFromDto(StudyGroupDto dto, @MappingTarget StudyGroup entity);
}
