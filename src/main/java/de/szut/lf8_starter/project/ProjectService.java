package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.EmployeeNotAvailableException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.GetEmployeesOfProjectDto;
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

import java.time.LocalDate;
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
     * Creates a new project after validating the provided data.
     *
     * @param createDto   DTO containing the project data.
     * @param bearerToken the authorization token for external validation.
     * @return the created project as a DTO.
     */
    public ProjectGetDto create(ProjectCreateDto createDto, String bearerToken) {
        validateEmployeeExists(createDto.getResponsibleEmployeeId(), bearerToken);
        if (createDto.getEmployeeIds() != null) {
            createDto.getEmployeeIds().forEach(employeeId -> validateEmployeeExists(employeeId, bearerToken));
        }

        validateCustomerExists(createDto.getCustomerId());

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
     * Updates an existing project after validating the provided data.
     *
     * @param id          the ID of the project to update.
     * @param updateDto   DTO with the new data.
     * @param bearerToken the authorization token for external validation.
     * @return the updated project DTO.
     */
    public ProjectGetDto update(Long id, ProjectCreateDto updateDto, String bearerToken) {
        if (updateDto.getResponsibleEmployeeId() != null) {
            validateEmployeeExists(updateDto.getResponsibleEmployeeId(), bearerToken);
        }
        if (updateDto.getEmployeeIds() != null) {
            updateDto.getEmployeeIds().forEach(employeeId -> validateEmployeeExists(employeeId, bearerToken));
        }

        validateCustomerExists(updateDto.getCustomerId());

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
     */
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }

    /**
     * Adds an employee to a project after validation.
     *
     * @param projectId   the ID of the project.
     * @param employeeId  the ID of the employee to add.
     * @param bearerToken the authorization token for external validation.
     * @return the updated project DTO.
     */
    public ProjectGetDto addEmployeeToProject(Long projectId, Long employeeId, String bearerToken) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        validateEmployeeExists(employeeId, bearerToken);
        checkEmployeeAvailability(employeeId, project.getStartDate(), project.getEndDate());

        if (project.getEmployeeIds().contains(employeeId)) {
            return projectMapper.mapEntityToGetDto(project);
        }

        project.getEmployeeIds().add(employeeId);
        ProjectEntity updatedProject = projectRepository.save(project);
        return projectMapper.mapEntityToGetDto(updatedProject);
    }

    /**
     * Checks if an employee is already scheduled during a given timeframe.
     * Throws an EmployeeNotAvailableException if a conflict is found.
     */
    private void checkEmployeeAvailability(Long employeeId, LocalDate newProjectStart, LocalDate newProjectEnd) {
        if (newProjectStart == null || newProjectEnd == null) {
            return;
        }

        List<ProjectEntity> projectsOfEmployee = projectRepository.findByEmployeeIdsContaining(employeeId);

        for (ProjectEntity existingProject : projectsOfEmployee) {
            if (existingProject.getStartDate() == null || existingProject.getEndDate() == null) {
                continue;
            }

            boolean overlaps = !newProjectStart.isAfter(existingProject.getEndDate()) &&
                    !newProjectEnd.isBefore(existingProject.getStartDate());

            if (overlaps) {
                throw new EmployeeNotAvailableException("Employee with ID " + employeeId + " is already scheduled in project '" + existingProject.getName() + "' during this timeframe.");
            }
        }
    }

    /**
     * Validates if an employee exists via the external employee service.
     * Throws a ResourceNotFoundException if the employee does not exist.
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

    /**
     * Removes an employee from a project.
     *
     * @param projectId  the ID of the project.
     * @param employeeId the ID of the employee to remove.
     */
    public void removeEmployeeFromProject(Long projectId, Long employeeId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        boolean removed = project.getEmployeeIds().remove(employeeId);

        if (!removed) {
            throw new ResourceNotFoundException("Employee with ID " + employeeId + " is not assigned to project with ID " + projectId + ".");
        }
        projectRepository.save(project);
    }

    public GetEmployeesOfProjectDto getEmployeesOfProject(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        GetEmployeesOfProjectDto dto = new GetEmployeesOfProjectDto();
        dto.setProjectId(project.getId());
        dto.setProjectName(project.getName());
        dto.setEmployeeIds(project.getEmployeeIds());

        return dto;
    }

    public List<ProjectGetDto> getProjectsOfEmployee(Long employeeId) {
        List<ProjectEntity> projects = projectRepository.findByEmployeeIdsContaining(employeeId);

        return projects.stream()
                .map(projectMapper::mapEntityToGetDto)
                .collect(Collectors.toList());
    }

    /**
     * DUMMY METHOD: Validates if a customer with the given ID exists.
     * This is a placeholder for a future call to the customer service.
     *
     * @param customerId The ID of the customer to validate.
     */
    private void validateCustomerExists(Long customerId) {
        if (customerId == null) {
            return;
        }
        System.out.println("--> Skipping customer validation (dummy method) for customerId: " + customerId);
    }
}