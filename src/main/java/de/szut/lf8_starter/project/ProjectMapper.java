package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Maps between Project DTOs (Data Transfer Objects) and ProjectEntity.
 */
@Component
public class ProjectMapper {

    /**
     * Maps a ProjectCreateDto to a new ProjectEntity.
     *
     * @param dto the DTO with the creation data
     * @return a new ProjectEntity ready to be saved
     */
    public ProjectEntity mapCreateDtoToEntity(ProjectCreateDto dto) {
        ProjectEntity entity = new ProjectEntity();
        return mapUpdateDtoToEntity(entity, dto);
    }

    /**
     * Updates an existing ProjectEntity with data from a ProjectCreateDto.
     *
     * @param entity the existing entity from the database
     * @param dto    the DTO with the update data
     * @return the updated ProjectEntity
     */
    public ProjectEntity mapUpdateDtoToEntity(ProjectEntity entity, ProjectCreateDto dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCustomerId(dto.getCustomerId());
        entity.setResponsibleEmployeeId(dto.getResponsibleEmployeeId());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.PLANNED);
        entity.setEmployeeIds(dto.getEmployeeIds() != null ? new HashSet<>(dto.getEmployeeIds()) : new HashSet<>());
        return entity;
    }

    /**
     * Maps a ProjectEntity to a ProjectGetDto.
     *
     * @param entity the entity from the database
     * @return a DTO suitable for sending to the client
     */
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
        dto.setEmployeeIds(new HashSet<>(entity.getEmployeeIds()));
        return dto;
    }
}