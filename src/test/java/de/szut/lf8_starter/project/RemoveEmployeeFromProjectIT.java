package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class RemoveEmployeeFromProjectIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    @Transactional
    public void removeEmployeeSuccessfully_HappyPath() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Projekt mit Team");
        project.setResponsibleEmployeeId(1L);
        Set<Long> employees = new HashSet<>();
        employees.add(2L);
        employees.add(3L);
        project.setEmployeeIds(employees);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();
        Long employeeToRemoveId = 3L;

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeToRemoveId)
                        .with(csrf())
                        .with(jwt()))
                .andExpect(status().isNoContent());

        ProjectEntity updatedProject = projectRepository.findById(projectId).orElseThrow();
        assertThat(updatedProject.getEmployeeIds()).hasSize(1);
        assertThat(updatedProject.getEmployeeIds()).contains(2L);
        assertThat(updatedProject.getEmployeeIds()).doesNotContain(employeeToRemoveId);
    }
}