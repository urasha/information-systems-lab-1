package ru.urasha.studygroup.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.urasha.studygroup.models.*;

@Data
public class StudyGroupDto {

    @NotBlank
    private String name;

    @NotNull
    private Coordinates coordinates;

    @Min(1)
    private int studentsCount = 1;

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
    private Person groupAdmin;
}
