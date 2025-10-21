package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteProjectIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    public void deleteProjectSuccessfully() throws Exception {
        ProjectEntity projectToDelete = new ProjectEntity();
        projectToDelete.setName("Projekt zum Löschen");
        projectToDelete.setResponsibleEmployeeId(1L);
        ProjectEntity savedProject = projectRepository.save(projectToDelete);
        Long id = savedProject.getId();

        mockMvc.perform(delete("/projects/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        
        assertThat(projectRepository.findById(id)).isEmpty();
    }
}