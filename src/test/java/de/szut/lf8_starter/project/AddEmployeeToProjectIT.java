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

import java.util.HashSet;

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
}