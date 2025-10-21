package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.AddEmployeeToProjectDto;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling project-related HTTP requests.
 * Implements the OpenAPI definition from ProjectControllerOpenAPI.
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController implements ProjectControllerOpenAPI {

    private final ProjectService projectService;

    @Override
    @PostMapping
    public ResponseEntity<ProjectGetDto> createProject(
            @Valid @RequestBody ProjectCreateDto createDto,
            @RequestHeader("Authorization") String bearerToken) {
        ProjectGetDto createdProject = projectService.create(createDto, bearerToken);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ProjectGetDto>> getAllProjects() {
        List<ProjectGetDto> projects = projectService.readAll();
        return ResponseEntity.ok(projects);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProjectGetDto> getProjectById(@PathVariable Long id) {
        ProjectGetDto project = projectService.readById(id);
        return ResponseEntity.ok(project);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ProjectGetDto> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateDto updateDto,
            @RequestHeader("Authorization") String bearerToken) {

        ProjectGetDto updatedProject = projectService.update(id, updateDto, bearerToken);
        return ResponseEntity.ok(updatedProject);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.delete(id);
    }

    @Override
    @PostMapping("/{projectId}/employees")
    public ResponseEntity<ProjectGetDto> addEmployeeToProject(@PathVariable Long projectId,
                                                              @Valid @RequestBody AddEmployeeToProjectDto dto,
                                                              @RequestHeader("Authorization") String bearerToken) {
        ProjectGetDto updatedProject = projectService.addEmployeeToProject(projectId, dto.getEmployeeId(), bearerToken);
        return ResponseEntity.ok(updatedProject);
    }
}