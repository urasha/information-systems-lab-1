package ru.urasha.studygroup.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.urasha.studygroup.models.*;

@Data
public class StudyGroupDto {

    @NotBlank
    private String name;

    @NotNull
    @Valid
    private Coordinates coordinates;

    @Min(0)
    private int studentsCount = 0;

    @Min(1)
    private long expelledStudents = 1;

    @Min(1)
    private int transferredStudents = 1;

    private FormOfEducation formOfEducation;

    @NotNull
    @Min(1)
    private Integer shouldBeExpelled = 1;

    @Min(1)
    private double averageMark = 1.0;

    @NotNull
    private Semester semesterEnum;

    @NotNull
    @Valid
    private Person groupAdmin;
}
