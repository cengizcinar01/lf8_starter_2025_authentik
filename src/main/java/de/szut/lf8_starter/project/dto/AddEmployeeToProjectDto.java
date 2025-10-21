package de.szut.lf8_starter.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for adding an employee to a project.
 */
@Data
public class AddEmployeeToProjectDto {

    @NotNull(message = "Employee ID is mandatory.")
    private Long employeeId;
}