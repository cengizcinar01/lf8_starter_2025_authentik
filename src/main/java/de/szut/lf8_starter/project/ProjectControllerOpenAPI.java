package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ErrorDetails;
import de.szut.lf8_starter.project.dto.AddEmployeeToProjectDto;
import de.szut.lf8_starter.project.dto.GetEmployeesOfProjectDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * OpenAPI definition for the Project Controller.
 * This interface defines all endpoints related to projects, including their documentation.
 */
public interface ProjectControllerOpenAPI {

    @Operation(summary = "Creates a new project.", description = "Creates a new project with the given data. Validates the existence of the responsible employee and all team members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectGetDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., name is missing)", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))}),
            @ApiResponse(responseCode = "401", description = "Not authorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "A specified employee was not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    ResponseEntity<ProjectGetDto> createProject(@Valid @RequestBody ProjectCreateDto createDto,
                                                @RequestHeader("Authorization") String bearerToken);

    @Operation(summary = "Gets a list of all projects.", description = "Retrieves a complete list of all projects available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authorized", content = @Content)
    })
    ResponseEntity<List<ProjectGetDto>> getAllProjects();

    @Operation(summary = "Gets a single project by its ID.", description = "Retrieves the full details of a specific project by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectGetDto.class))}),
            @ApiResponse(responseCode = "401", description = "Not authorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project with the given ID not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    ResponseEntity<ProjectGetDto> getProjectById(@PathVariable Long id);

    @Operation(summary = "Updates an existing project.", description = "Updates the details of an existing project identified by its ID. All fields are replaced with the new data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectGetDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))}),
            @ApiResponse(responseCode = "401", description = "Not authorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project or a specified employee not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    ResponseEntity<ProjectGetDto> updateProject(@PathVariable Long id,
                                                @Valid @RequestBody ProjectCreateDto updateDto,
                                                @RequestHeader("Authorization") String bearerToken);

    @Operation(summary = "Deletes a project by its ID.", description = "Permanently deletes a project from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project with the given ID not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProject(@PathVariable Long id);

    @Operation(summary = "Adds an employee to a project team.", description = "Assigns an existing employee to an existing project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee added successfully", content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Project or employee not found"),
            @ApiResponse(responseCode = "409", description = "Conflict, e.g., employee is already in the team or scheduled in this timeframe")
    })
    ResponseEntity<ProjectGetDto> addEmployeeToProject(@PathVariable Long projectId,
                                                       @Valid @RequestBody AddEmployeeToProjectDto dto,
                                                       @RequestHeader("Authorization") String bearerToken);

    @Operation(summary = "Removes an employee from a project team.", description = "Removes a specific employee from a specific project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee removed successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project or employee assignment not found", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeEmployeeFromProject(@PathVariable Long projectId, @PathVariable Long employeeId);

    @Operation(summary = "Gets all employees of a project.", description = "Retrieves a list of all employee IDs assigned to a specific project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee list retrieved successfully", content = @Content(schema = @Schema(implementation = GetEmployeesOfProjectDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    ResponseEntity<GetEmployeesOfProjectDto> getEmployeesOfProject(@PathVariable Long projectId);
}