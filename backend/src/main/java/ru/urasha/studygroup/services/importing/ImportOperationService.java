package ru.urasha.studygroup.services.importing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.urasha.studygroup.models.importing.ImportOperation;
import ru.urasha.studygroup.models.importing.ImportStatus;
import ru.urasha.studygroup.repositories.ImportOperationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImportOperationService {

    private final ImportOperationRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportOperation createRunningOperation(String username, String role) {
        ImportOperation importOperation = new ImportOperation();
        importOperation.setStatus(ImportStatus.RUNNING);
        importOperation.setUsername(username);
        importOperation.setRole(role);
        importOperation.setCreatedAt(LocalDateTime.now());

        return repository.save(importOperation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(Long operationId, int importedCount) {
        Optional<ImportOperation> optionalImportOperation = repository.findById(operationId);
        if (optionalImportOperation.isEmpty()) {
            return;
        }

        ImportOperation importOperation = optionalImportOperation.get();
        importOperation.setStatus(ImportStatus.COMPLETED);
        importOperation.setImportedCount(importedCount);
        importOperation.setFinishedAt(LocalDateTime.now());

        repository.save(importOperation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long operationId, String shortErrorMessage) {
        Optional<ImportOperation> optionalImportOperation = repository.findById(operationId);
        if (optionalImportOperation.isEmpty()) {
            return;
        }

        ImportOperation importOperation = optionalImportOperation.get();
        importOperation.setStatus(ImportStatus.FAILED);
        importOperation.setErrorMessage(shortErrorMessage);
        importOperation.setFinishedAt(LocalDateTime.now());

        repository.save(importOperation);
    }
}
