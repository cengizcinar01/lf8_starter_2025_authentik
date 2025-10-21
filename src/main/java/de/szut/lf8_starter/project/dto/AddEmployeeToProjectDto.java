package de.szut.lf8_starter.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for adding a single employee to a project.
 * Used as the request body for POST /projects/{projectId}/employees.
 */
@Data
public class AddEmployeeToProjectDto {

    @NotNull(message = "Employee ID is mandatory.")
    private Long employeeId;
}