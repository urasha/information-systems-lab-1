package ru.urasha.studygroup.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urasha.studygroup.models.StudyGroup;
import ru.urasha.studygroup.services.SpecialOpsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/special")
@RequiredArgsConstructor
public class SpecialOpsController {

    private final SpecialOpsService specialOpsService;

    @DeleteMapping("/by-admin")
    public ResponseEntity<Void> deleteByAdmin(@RequestParam String adminName) {
        specialOpsService.deleteByAdminName(adminName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudyGroup>> searchByName(@RequestParam String substring) {
        List<StudyGroup> studyGroups = specialOpsService.searchByName(substring);
        return ResponseEntity.ok(studyGroups);
    }

    @GetMapping("/unique-admins")
    public ResponseEntity<List<String>> uniqueAdmins() {
        List<String> uniqueAdminNames = specialOpsService.getUniqueAdminNames();
        return ResponseEntity.ok(uniqueAdminNames);
    }

    @PostMapping("/{groupId}/expel")
    public ResponseEntity<StudyGroup> expelAll(@PathVariable Integer groupId) {
        StudyGroup updatedGroup = specialOpsService.expelAllStudents(groupId);
        return ResponseEntity.ok(updatedGroup);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, StudyGroup>> transfer(
            @RequestParam Integer fromId,
            @RequestParam Integer toId
    ) {
        Map<String, StudyGroup> result = specialOpsService.transferAllStudents(fromId, toId);
        return ResponseEntity.ok(result);
    }
}
