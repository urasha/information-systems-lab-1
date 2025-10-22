package ru.urasha.studygroup.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final StudyGroupRepository repository;
    private final StudyGroupMapper mapper;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    @Transactional
    public ImportResultDto importFromFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Empty or missing file")
            ));
        }

        String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown");
        try {
            if (filename.toLowerCase().endsWith(".json")
                    || "application/json".equalsIgnoreCase(file.getContentType())
            ) {
                List<StudyGroupDto> dtos;

                try {
                    dtos = objectMapper.readValue(
                            file.getInputStream(),
                            new TypeReference<>() {
                            }
                    );
                } catch (JsonProcessingException jpe) {
                    String detailedMessage = getDetailedJsonErrorMessage(jpe);
                    List<ImportErrorDto> errors = Collections.singletonList(
                            new ImportErrorDto(-1, "json", detailedMessage)
                    );

                    throw new ImportException(errors);
                }

                List<ImportErrorDto> errors = new ArrayList<>();

                for (int i = 0; i < dtos.size(); i++) {
                    StudyGroupDto dto = dtos.get(i);
                    Set<ConstraintViolation<StudyGroupDto>> violations = validator.validate(dto);
                    for (ConstraintViolation<StudyGroupDto> v : violations) {
                        String path = v.getPropertyPath().toString();
                        errors.add(new ImportErrorDto(i, path, v.getMessage()));
                    }
                }

                if (!errors.isEmpty()) {
                    throw new ImportException(errors);
                }

                List<StudyGroup> entities = dtos.stream()
                        .map(mapper::toEntity)
                        .collect(Collectors.toList());

                repository.saveAll(entities);

                return new ImportResultDto(entities.size(), "Imported successfully");

            } else {
                throw new ImportException(Collections.singletonList(
                        new ImportErrorDto(-1, "file", "Unsupported file type. Use .json")
                ));
            }
        } catch (IOException e) {
            log.error("IO error while importing file '{}': {}", filename, e.getMessage(), e);
            throw new ImportException(Collections.singletonList(
                    new ImportErrorDto(-1, "file", "Cannot read file: " + e.getMessage())
            ));
        }
    }

    private String getDetailedJsonErrorMessage(JsonProcessingException jpe) {
        String originalMessage = jpe.getOriginalMessage();

        if (originalMessage != null) {
            if (originalMessage.contains("Unexpected character") ||
                    originalMessage.contains("unexpected close marker") ||
                    originalMessage.contains("Unexpected end-of-input")) {
                return "Syntax error in JSON: " + originalMessage;
            }

            if (originalMessage.contains("Cannot deserialize") ||
                    originalMessage.contains("Unrecognized field")) {
                return "JSON structure error: " + originalMessage;
            }
        }

        return "Invalid JSON format: " + (originalMessage != null ? originalMessage : jpe.getMessage());
    }
}
