package de.szut.lf8_starter.project.dto;

import de.szut.lf8_starter.project.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectCreateDto {

    @NotBlank(message = "Project name is required")
    private String name;
    
    private String description;
    
    private Long customerId;

    @NotNull(message = "Responsible employee is required")
    private Long responsibleEmployeeId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private ProjectStatus status;
    
    private Set<Long> employeeIds;
}