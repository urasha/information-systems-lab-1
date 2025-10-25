package ru.urasha.studygroup.services.importing;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.urasha.studygroup.dto.ImportErrorDto;
import ru.urasha.studygroup.dto.ImportOperationDto;
import ru.urasha.studygroup.dto.ImportResultDto;
import ru.urasha.studygroup.dto.StudyGroupDto;
import ru.urasha.studygroup.exceptions.ImportException;
import ru.urasha.studygroup.mappers.StudyGroupMapper;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.models.importing.ImportOperation;
import ru.urasha.studygroup.repositories.ImportOperationRepository;
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
    private final ImportOperationRepository importOperationRepository;
    private final StudyGroupMapper mapper;
    private final ObjectMapper objectMapper;
    private final ImportValidator importValidator;
    private final ImportOperationService operationService;

    @Transactional
    public ImportResultDto importFromFile(MultipartFile file, String username, String role) {
        if (file == null || file.isEmpty()) {
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Empty or missing file")
            ));
        }

        String filename = file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename();

        var op = operationService.createRunningOperation(username, role);
        Long opId = op.getId();

        try {
            List<StudyGroupDto> dtos = parseDtos(file, filename);

            List<ImportErrorDto> validationErrors = importValidator.validateAll(dtos);
            if (!validationErrors.isEmpty()) {
                operationService.markFailed(opId, "Validation failed: " + validationErrors.size() + " error(s)");
                throw new ImportException(validationErrors);
            }

            List<StudyGroup> entities = dtos.stream()
                    .map(mapper::toEntity)
                    .collect(Collectors.toList());

            repository.saveAll(entities);

            operationService.markCompleted(opId, entities.size());

            return new ImportResultDto(entities.size(), "Imported successfully");
        } catch (ImportException exception) {
            String shortMsg = "Import failed (validation or parse error)";
            operationService.markFailed(opId, shortMsg);
            throw exception;
        } catch (Exception ex) {
            log.warn("Import failed for user {}: {}", username, ex.getMessage());
            operationService.markFailed(opId, "Import failed: " + (ex.getMessage() == null ? "unknown error" : ex.getMessage()));
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Import failed due to server error")
            ));
        }
    }

    public List<ImportOperationDto> listOperations(String username, String role) {
        List<ImportOperation> allOperations;
        if ("ADMIN".equalsIgnoreCase(role)) {
            allOperations = importOperationRepository.findAllByOrderByCreatedAtDesc();
        } else {
            allOperations = importOperationRepository.findByUsernameOrderByCreatedAtDesc(username);
        }

        return allOperations.stream()
                .map(operation -> new ImportOperationDto(
                        operation.getId(),
                        operation.getStatus() == null ? null : operation.getStatus().name(),
                        operation.getUsername(),
                        operation.getImportedCount(),
                        operation.getErrorMessage(),
                        operation.getCreatedAt(),
                        operation.getFinishedAt()
                ))
                .collect(Collectors.toList());
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
            log.warn("JSON mapping error in file '{}': {} (field={}, index={})",
                    filename, jme.getOriginalMessage(), err.field(), err.index());
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