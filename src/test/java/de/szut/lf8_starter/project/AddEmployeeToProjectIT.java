package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class AddEmployeeToProjectIT extends AbstractIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @WithMockUser
    public void addEmployeeSuccessfully_HappyPath() throws Exception {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(Void.class), eq(3L)))
                .thenReturn(ResponseEntity.ok().build());

        ProjectEntity project = new ProjectEntity();
        project.setName("Testprojekt");
        project.setResponsibleEmployeeId(1L);
        project.setEmployeeIds(new HashSet<>());
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        String requestJson = """
                {
                  "employeeId": 3
                }
                """;

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                        .with(csrf())
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeIds", hasItem(3)));
    }

    @Test
    @WithMockUser
    public void addEmployeeFails_SchedulingConflict() throws Exception {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(Void.class), eq(5L)))
                .thenReturn(ResponseEntity.ok().build());

        ProjectEntity existingProject = new ProjectEntity();
        existingProject.setName("Altes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setStartDate(LocalDate.parse("2025-01-01"));
        existingProject.setEndDate(LocalDate.parse("2025-01-31"));
        existingProject.setEmployeeIds(Set.of(5L));
        projectRepository.save(existingProject);

        ProjectEntity newProject = new ProjectEntity();
        newProject.setName("Neues Projekt");
        newProject.setResponsibleEmployeeId(2L);
        newProject.setStartDate(LocalDate.parse("2025-01-15"));
        newProject.setEndDate(LocalDate.parse("2025-02-15"));
        newProject.setEmployeeIds(new HashSet<>());
        ProjectEntity savedNewProject = projectRepository.save(newProject);
        Long newProjectId = savedNewProject.getId();

        String requestJson = """
                {
                  "employeeId": 5
                }
                """;

        mockMvc.perform(post("/projects/{projectId}/employees", newProjectId)
                        .with(csrf())
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }
}