package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling project-related business logic.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    /**
     * Creates a new project after validating the provided employee IDs.
     *
     * @param createDto DTO containing the project data.
     * @return the created project as a DTO.
     */
    public ProjectGetDto create(ProjectCreateDto createDto) {
        ProjectEntity newEntity = projectMapper.mapCreateDtoToEntity(createDto);
        ProjectEntity savedEntity = projectRepository.save(newEntity);
        return projectMapper.mapEntityToGetDto(savedEntity);
    }

    /**
     * Retrieves all projects.
     *
     * @return a list of all projects.
     */
    public List<ProjectGetDto> readAll() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::mapEntityToGetDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single project by its ID.
     *
     * @param id the ID of the project.
     * @return the project DTO.
     * @throws ResourceNotFoundException if no project with the given ID is found.
     */
    public ProjectGetDto readById(Long id) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
        return projectMapper.mapEntityToGetDto(entity);
    }

    /**
     * Updates an existing project.
     *
     * @param id        the ID of the project to update.
     * @param updateDto DTO with the new data.
     * @return the updated project DTO.
     * @throws ResourceNotFoundException if the project to update is not found.
     */
    public ProjectGetDto update(Long id, ProjectCreateDto updateDto) {
        ProjectEntity existingEntity = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
        ProjectEntity updatedEntity = projectMapper.mapUpdateDtoToEntity(existingEntity, updateDto);
        ProjectEntity savedEntity = projectRepository.save(updatedEntity);
        return projectMapper.mapEntityToGetDto(savedEntity);
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id the ID of the project to delete.
     * @throws ResourceNotFoundException if the project to delete is not found.
     */
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }
}