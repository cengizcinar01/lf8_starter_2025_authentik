package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class GetProjectByIdIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    public void getProjectByIdSuccessfully() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Spezialprojekt");
        project.setResponsibleEmployeeId(3L);
        project.setCustomerId(101L);
        ProjectEntity savedProject = projectRepository.save(project);
        Long id = savedProject.getId();

        mockMvc.perform(get("/projects/{id}", id)
                        .with(csrf())
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is("Spezialprojekt")))
                .andExpect(jsonPath("$.customerId", is(101)));
    }
}