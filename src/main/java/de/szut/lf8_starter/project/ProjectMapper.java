package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectEntity mapCreateDtoToEntity(ProjectCreateDto dto) {
        ProjectEntity entity = new ProjectEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCustomerId(dto.getCustomerId());
        entity.setResponsibleEmployeeId(dto.getResponsibleEmployeeId());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(dto.getStatus());
        entity.setEmployeeIds(dto.getEmployeeIds());
        return entity;
    }

    public ProjectGetDto mapEntityToGetDto(ProjectEntity entity) {
        ProjectGetDto dto = new ProjectGetDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCustomerId(entity.getCustomerId());
        dto.setResponsibleEmployeeId(entity.getResponsibleEmployeeId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        dto.setEmployeeIds(entity.getEmployeeIds());
        return dto;
    }
}