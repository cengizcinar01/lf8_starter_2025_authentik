package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetProjectByIdIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    public void getProjectByIdSuccessfully() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Spezialprojekt");
        project.setDescription("Ein Test mit allen Feldern");
        project.setResponsibleEmployeeId(3L);
        project.setCustomerId(101L);
        project.setStartDate(LocalDate.parse("2025-01-01"));
        project.setEmployeeIds(Set.of(3L, 4L));
        ProjectEntity savedProject = projectRepository.save(project);
        Long id = savedProject.getId();

        mockMvc.perform(get("/projects/{id}", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is("Spezialprojekt")))
                .andExpect(jsonPath("$.description", is("Ein Test mit allen Feldern")))
                .andExpect(jsonPath("$.responsibleEmployeeId", is(3)))
                .andExpect(jsonPath("$.customerId", is(101)))
                .andExpect(jsonPath("$.startDate", is("2025-01-01")));
    }
}