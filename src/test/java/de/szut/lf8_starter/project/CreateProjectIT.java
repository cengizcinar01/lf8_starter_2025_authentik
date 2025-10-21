package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class CreateProjectIT extends AbstractIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @WithMockUser
    @Transactional
    public void createProjectSuccessfully() throws Exception {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(Void.class), any(Long.class)))
                .thenReturn(ResponseEntity.ok().build());

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
                        .header("Authorization", "Bearer dummy-token")
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
        assertThat(savedProject.getDescription()).isEqualTo("Dies ist ein Test");
        assertThat(savedProject.getCustomerId()).isEqualTo(42);
        assertThat(savedProject.getEmployeeIds()).containsExactlyInAnyOrder(1L, 5L, 7L);
    }

    @Test
    @WithMockUser
    public void createProjectFailsWithInvalidResponsibleEmployeeId() throws Exception {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(Void.class), eq(99999L)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        String projectJson = """
                {
                  "name": "Projekt mit ungültigem Verantwortlichen",
                  "responsibleEmployeeId": 99999
                }
                """;

        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee with ID 99999 not found.")));
    }
}