package de.szut.lf8_starter.project.dto;

import de.szut.lf8_starter.project.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectGetDto {

    private Long id;

    private String name;

    private String description;

    private Long customerId;

    private Long responsibleEmployeeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private ProjectStatus status;

    private Set<Long> employeeIds;
}