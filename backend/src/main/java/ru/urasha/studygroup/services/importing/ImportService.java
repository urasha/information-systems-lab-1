package ru.urasha.studygroup.services.importing;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urasha.studygroup.dto.ImportErrorDto;
import ru.urasha.studygroup.dto.ImportResultDto;
import ru.urasha.studygroup.dto.StudyGroupDto;
import ru.urasha.studygroup.exceptions.ImportException;
import ru.urasha.studygroup.mappers.StudyGroupMapper;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.repositories.StudyGroupRepository;
import ru.urasha.studygroup.util.JsonErrorParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final StudyGroupRepository repository;
    private final StudyGroupMapper mapper;
    private final ObjectMapper objectMapper;
    private final ImportValidator importValidator;

    @Transactional
    public ImportResultDto importFromFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Empty or missing file")
            ));
        }

        String filename = safeFilename(file.getOriginalFilename());

        validateFileType(filename, file.getContentType());

        List<StudyGroupDto> dtos = parseDtos(file, filename);

        List<ImportErrorDto> errors = importValidator.validateAll(dtos);
        if (!errors.isEmpty()) {
            throw new ImportException(errors);
        }

        List<StudyGroup> entities = dtos.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());

        repository.saveAll(entities);

        return new ImportResultDto(entities.size(), "Imported successfully");
    }

    private String safeFilename(String raw) {
        return raw == null ? "unknown" : raw;
    }

    private void validateFileType(String filename, String contentType) {
        boolean jsonByName = filename.toLowerCase().endsWith(".json");
        boolean jsonByType = "application/json".equalsIgnoreCase(contentType);
        if (!(jsonByName || jsonByType)) {
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Unsupported file type. Use .json")
            ));
        }
    }

    private List<StudyGroupDto> parseDtos(MultipartFile file, String filename) {
        try {
            return objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<>() {
                    }
            );
        } catch (JsonParseException jpe) {
            log.warn("Invalid JSON structure in file '{}': {}", filename, jpe.getOriginalMessage());
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Invalid JSON structure: check syntax (braces, commas, etc.)")
            ));
        } catch (JsonMappingException jme) {
            ImportErrorDto err = JsonErrorParser.fromJsonMappingException(jme);
            log.warn("JSON mapping error in file '{}': field='{}', record={}",
                    filename, err.field(), err.index());
            throw new ImportException(Collections.singletonList(err));
        } catch (JsonProcessingException jpe) {
            log.error("JSON processing error in file '{}': {}", filename, jpe.getOriginalMessage());
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Invalid JSON format")
            ));
        } catch (IOException ioe) {
            log.error("IO error while reading file '{}': {}", filename, ioe.getMessage());
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Cannot read file")
            ));
        }
    }
}