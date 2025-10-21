package de.szut.lf8_starter.project.dto;

import de.szut.lf8_starter.project.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for creating and updating a project.
 * This object is received from the client.
 */
@Data
public class ProjectCreateDto {

    /**
     * The name of the project. Cannot be empty.
     * Max length is 255 characters.
     */
    @NotBlank(message = "Project name is mandatory and cannot be empty.")
    @Size(max = 255, message = "Project name must not exceed 255 characters.")
    private String name;

    /**
     * A detailed description of the project.
     * Max length is 2000 characters.
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    private String description;

    /**
     * The ID of the customer.
     */
    private Long customerId;

    /**
     * The ID of the responsible employee. This is a mandatory field.
     */
    @NotNull(message = "Responsible employee ID is mandatory.")
    private Long responsibleEmployeeId;

    /**
     * The start date of the project. Can be in the future.
     */
    private LocalDate startDate;

    /**
     * The planned end date of the project.
     */
    private LocalDate endDate;

    /**
     * The initial status of the project. If not provided, it defaults to PLANNED.
     */
    private ProjectStatus status;

    /**
     * A set of employee IDs assigned to the project.
     */
    private Set<Long> employeeIds;
}