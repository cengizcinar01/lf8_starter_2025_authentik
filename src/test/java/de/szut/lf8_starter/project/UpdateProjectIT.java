package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateProjectIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    @Transactional
    public void updateProjectSuccessfully() throws Exception {
        ProjectEntity originalProject = new ProjectEntity();
        originalProject.setName("Altes Projekt");
        originalProject.setResponsibleEmployeeId(1L);
        ProjectEntity savedProject = projectRepository.save(originalProject);
        Long id = savedProject.getId();

        String updateJson = """
                {
                  "name": "Neues, aktualisiertes Projekt",
                  "description": "Die Beschreibung wurde hinzugefügt.",
                  "responsibleEmployeeId": 5
                }
                """;

        mockMvc.perform(put("/projects/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Neues, aktualisiertes Projekt")))
                .andExpect(jsonPath("$.description", is("Die Beschreibung wurde hinzugefügt.")))
                .andExpect(jsonPath("$.responsibleEmployeeId", is(5)));
        
        ProjectEntity updatedProjectFromDb = projectRepository.findById(id).orElseThrow();
        assertThat(updatedProjectFromDb.getName()).isEqualTo("Neues, aktualisiertes Projekt");
        assertThat(updatedProjectFromDb.getDescription()).isEqualTo("Die Beschreibung wurde hinzugefügt.");
    }
}