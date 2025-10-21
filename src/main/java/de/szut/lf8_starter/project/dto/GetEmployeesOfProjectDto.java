package de.szut.lf8_starter.project.dto;

import lombok.Data;

import java.util.Set;

/**
 * Data Transfer Object for retrieving the list of employees for a specific project.
 * Used as the response body for GET /projects/{projectId}/employees.
 */
@Data
public class GetEmployeesOfProjectDto {
    private Long projectId;
    private String projectName;
    private Set<Long> employeeIds;
}