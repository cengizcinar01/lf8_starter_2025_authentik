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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling all project-related business logic.
 * This includes CRUD operations, validation, and interaction with external services.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final RestTemplate restTemplate;

    /**
     * Creates a new project after validating all provided data.
     * Note: Employee validation is performed sequentially due to external API constraints.
     * If the external API supports batch validation, this should be refactored for better performance.
     *
     * @param createDto   DTO containing the project data.
     * @param bearerToken the authorization token for external validation.
     * @return the created project as a DTO.
     */
    @Transactional
    public ProjectGetDto create(ProjectCreateDto createDto, String bearerToken) {
        validateEmployeeExists(createDto.getResponsibleEmployeeId(), bearerToken);
        if (createDto.getEmployeeIds() != null) {
            // Note: Sequential validation - consider batch API if available for better performance
            createDto.getEmployeeIds().forEach(employeeId -> validateEmployeeExists(employeeId, bearerToken));
        }
        validateCustomerExists(createDto.getCustomerId());

        ProjectEntity newEntity = projectMapper.mapCreateDtoToEntity(createDto);
        ProjectEntity savedEntity = projectRepository.save(newEntity);
        return projectMapper.mapEntityToGetDto(savedEntity);
    }

    /**
     * Retrieves a list of all projects.
     *
     * @return a list of all projects.
     */
    @Transactional(readOnly = true)
    public List<ProjectGetDto> readAll() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::mapEntityToGetDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single project by its unique ID.
     *
     * @param id the ID of the project.
     * @return the project DTO.
     */
    @Transactional(readOnly = true)
    public ProjectGetDto readById(Long id) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
        return projectMapper.mapEntityToGetDto(entity);
    }

    /**
     * Updates an existing project after validating all provided data.
     * Note: Employee validation is performed sequentially due to external API constraints.
     * If the external API supports batch validation, this should be refactored for better performance.
     *
     * @param id          the ID of the project to update.
     * @param updateDto   DTO with the new data.
     * @param bearerToken the authorization token for external validation.
     * @return the updated project DTO.
     */
    @Transactional
    public ProjectGetDto update(Long id, ProjectCreateDto updateDto, String bearerToken) {
        if (updateDto.getResponsibleEmployeeId() != null) {
            validateEmployeeExists(updateDto.getResponsibleEmployeeId(), bearerToken);
        }
        if (updateDto.getEmployeeIds() != null) {
            // Note: Sequential validation - consider batch API if available for better performance
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
    @Transactional
    public void delete(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
        projectRepository.delete(project);
    }

    /**
     * Adds a single employee to a project's team after performing all necessary validations.
     *
     * @param projectId   the ID of the project.
     * @param employeeId  the ID of the employee to add.
     * @param bearerToken the authorization token for external validation.
     * @return the updated project DTO.
     */
    @Transactional
    public ProjectGetDto addEmployeeToProject(Long projectId, Long employeeId, String bearerToken) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        validateEmployeeExists(employeeId, bearerToken);
        checkEmployeeAvailability(employeeId, project.getStartDate(), project.getEndDate(), projectId);

        if (project.getEmployeeIds().contains(employeeId)) {
            return projectMapper.mapEntityToGetDto(project);
        }

        project.getEmployeeIds().add(employeeId);
        ProjectEntity updatedProject = projectRepository.save(project);
        return projectMapper.mapEntityToGetDto(updatedProject);
    }

    /**
     * Removes a single employee from a project's team.
     *
     * @param projectId  the ID of the project.
     * @param employeeId the ID of the employee to remove.
     */
    @Transactional
    public void removeEmployeeFromProject(Long projectId, Long employeeId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        boolean removed = project.getEmployeeIds().remove(employeeId);

        if (!removed) {
            throw new ResourceNotFoundException("Employee with ID " + employeeId + " is not assigned to project with ID " + projectId + ".");
        }
        projectRepository.save(project);
    }

    /**
     * Retrieves all employees assigned to a specific project.
     *
     * @param projectId the ID of the project.
     * @return a DTO containing the project details and its employee IDs.
     */
    @Transactional(readOnly = true)
    public GetEmployeesOfProjectDto getEmployeesOfProject(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        GetEmployeesOfProjectDto dto = new GetEmployeesOfProjectDto();
        dto.setProjectId(project.getId());
        dto.setProjectName(project.getName());
        dto.setEmployeeIds(project.getEmployeeIds());

        return dto;
    }

    /**
     * Retrieves all projects a specific employee is involved in (as responsible or team member).
     * Optimized to use a single database query instead of two separate ones.
     *
     * @param employeeId the ID of the employee.
     * @return a list of project DTOs.
     */
    @Transactional(readOnly = true)
    public List<ProjectGetDto> getProjectsOfEmployee(Long employeeId) {
        return projectRepository.findAllProjectsByEmployeeId(employeeId)
                .stream()
                .map(projectMapper::mapEntityToGetDto)
                .collect(Collectors.toList());
    }

    /**
     * Checks if an employee is already scheduled for another project during the given timeframe.
     * Throws an EmployeeNotAvailableException if a scheduling conflict is found.
     * Optimized to use a single database query.
     */
    private void checkEmployeeAvailability(Long employeeId, LocalDate newProjectStart, LocalDate newProjectEnd, Long currentProjectId) {
        if (newProjectStart == null || newProjectEnd == null) {
            return;
        }

        projectRepository.findAllProjectsByEmployeeId(employeeId)
                .forEach(existingProject -> {
                    if (existingProject.getId().equals(currentProjectId)) {
                        return;
                    }
                    if (existingProject.getStartDate() == null || existingProject.getEndDate() == null) {
                        return;
                    }

                    boolean overlaps = !newProjectStart.isAfter(existingProject.getEndDate()) &&
                            !newProjectEnd.isBefore(existingProject.getStartDate());

                    if (overlaps) {
                        throw new EmployeeNotAvailableException("Employee with ID " + employeeId + " is already scheduled in project '" + existingProject.getName() + "' during this timeframe.");
                    }
                });
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
     * DUMMY METHOD: Validates if a customer with the given ID exists.
     */
    private void validateCustomerExists(Long customerId) {
        if (customerId == null) {
            return;
        }
        System.out.println("--> Skipping customer validation (dummy method) for customerId: " + customerId);
    }
}