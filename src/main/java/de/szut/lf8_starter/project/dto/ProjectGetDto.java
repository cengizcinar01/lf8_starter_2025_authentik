package de.szut.lf8_starter.project.dto;

import de.szut.lf8_starter.project.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for retrieving project data.
 * This object is sent to the client when project information is requested.
 */
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