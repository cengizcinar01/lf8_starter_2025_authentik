package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateProjectIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    @Transactional
    public void createProjectSuccessfully() throws Exception {
        String projectJson = """
                {
                  "name": "Test 1",
                  "description": "Dies ist ein Test",
                  "customerId": 42,
                  "responsibleEmployeeId": 1,
                  "startDate": "2028-01-01",
                  "employeeIds": [1, 5, 7]
                }
                """;

        String responseContent = mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Test 1")))
                .andExpect(jsonPath("$.status", is("PLANNED")))
                .andReturn().getResponse().getContentAsString();

        Long newProjectId = Long.parseLong(new JSONObject(responseContent).get("id").toString());

        ProjectEntity savedProject = projectRepository.findById(newProjectId).orElse(null);

        assertThat(savedProject).isNotNull();
        assertThat(savedProject.getName()).isEqualTo("Test 1");
        assertThat(savedProject.getCustomerId()).isEqualTo(42);
        assertThat(savedProject.getEmployeeIds()).containsExactlyInAnyOrder(1L, 5L, 7L);
    }
}