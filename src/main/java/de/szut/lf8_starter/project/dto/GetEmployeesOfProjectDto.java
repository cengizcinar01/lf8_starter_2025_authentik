package de.szut.lf8_starter.project.dto;

import lombok.Data;
import java.util.Set;

@Data
public class GetEmployeesOfProjectDto {
    private Long projectId;
    private String projectName;
    private Set<Long> employeeIds;
}