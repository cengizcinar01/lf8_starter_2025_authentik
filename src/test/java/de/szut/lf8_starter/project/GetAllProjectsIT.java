package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class GetAllProjectsIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    public void getAllProjectsSuccessfully() throws Exception {
        ProjectEntity project1 = new ProjectEntity();
        project1.setName("Projekt Alpha");
        project1.setResponsibleEmployeeId(1L);
        projectRepository.save(project1);

        ProjectEntity project2 = new ProjectEntity();
        project2.setName("Projekt Beta");
        project2.setResponsibleEmployeeId(2L);
        projectRepository.save(project2);

        mockMvc.perform(get("/projects")
                        .with(csrf())
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Projekt Alpha")))
                .andExpect(jsonPath("$[1].name", is("Projekt Beta")));
    }
}