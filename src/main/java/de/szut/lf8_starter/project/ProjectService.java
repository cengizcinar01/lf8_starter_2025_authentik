package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;

    /**
     * Creates a new project after validating the provided employee IDs.
     *
     * @param createDto DTO containing the project data.
     * @return the created project as a DTO.
     */
    public ProjectGetDto create(ProjectCreateDto createDto, String bearerToken) {
        validateEmployeeExists(createDto.getResponsibleEmployeeId(), bearerToken);
        if (createDto.getEmployeeIds() != null) {
            createDto.getEmployeeIds().forEach(employeeId -> validateEmployeeExists(employeeId, bearerToken));
        }
        ProjectEntity newEntity = projectMapper.mapCreateDtoToEntity(createDto);
        ProjectEntity savedEntity = projectRepository.save(newEntity);
        return projectMapper.mapEntityToGetDto(savedEntity);
    }

    private void validateEmployeeExists(Long aLong) {
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
    public ProjectGetDto update(Long id, ProjectCreateDto updateDto, String bearerToken) {
        validateEmployeeExists(updateDto.getResponsibleEmployeeId(), bearerToken);
        if (updateDto.getEmployeeIds() != null) {
            updateDto.getEmployeeIds().forEach(employeeId -> validateEmployeeExists(employeeId, bearerToken));
        }
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

    /**
     * Validates if an employee with the given ID exists by calling the external employee service.
     * Throws a ResourceNotFoundException if the employee does not exist.
     *
     * @param employeeId the ID of the employee to check.
     */
    private void validateEmployeeExists(Long employeeId, String bearerToken) {
        final String url = "https://employee-api.szut.dev/employees/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.GET, entity, Void.class, employeeId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ResourceNotFoundException("Employee with ID " + employeeId + " not found.");
            }
            throw e;
        }
    }
}