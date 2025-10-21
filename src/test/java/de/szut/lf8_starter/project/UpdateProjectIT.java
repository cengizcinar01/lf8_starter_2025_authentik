package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class UpdateProjectIT extends AbstractIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @WithMockUser
    @Transactional
    public void updateProjectSuccessfully() throws Exception {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(Void.class), any(Long.class)))
                .thenReturn(ResponseEntity.ok().build());

        ProjectEntity originalProject = new ProjectEntity();
        originalProject.setName("Altes Projekt");
        originalProject.setResponsibleEmployeeId(1L);
        ProjectEntity savedProject = projectRepository.save(originalProject);
        Long id = savedProject.getId();

        String updateJson = """
                {
                  "name": "Neues, aktualisiertes Projekt",
                  "responsibleEmployeeId": 5
                }
                """;

        mockMvc.perform(put("/projects/{id}", id)
                        .with(csrf())
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Neues, aktualisiertes Projekt")))
                .andExpect(jsonPath("$.responsibleEmployeeId", is(5)));

        ProjectEntity updatedProjectFromDb = projectRepository.findById(id).orElseThrow();
        assertThat(updatedProjectFromDb.getName()).isEqualTo("Neues, aktualisiertes Projekt");
    }
}