package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * OpenAPI definition for the Project Controller.
 * This interface defines all endpoints related to projects, including their documentation.
 */
public interface ProjectControllerOpenAPI {

    @Operation(summary = "Creates a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully", content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., name is missing)"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "A specified employee was not found")
    })
    ResponseEntity<ProjectGetDto> createProject(@Valid @RequestBody ProjectCreateDto createDto);

    @Operation(summary = "Gets a list of all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authorized")
    })
    ResponseEntity<List<ProjectGetDto>> getAllProjects();

    @Operation(summary = "Gets a single project by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found", content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    ResponseEntity<ProjectGetDto> getProjectById(@PathVariable Long id);

    @Operation(summary = "Updates an existing project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully", content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Project or a specified employee not found")
    })
    ResponseEntity<ProjectGetDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectCreateDto updateDto);

    @Operation(summary = "Deletes a project by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProject(@PathVariable Long id);
}