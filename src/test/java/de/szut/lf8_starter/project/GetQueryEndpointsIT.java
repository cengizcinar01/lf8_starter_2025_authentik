package de.szut.lf8_starter.project;

import de.szut.lf8_starter.config.TestSecurityConfiguration;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
public class GetQueryEndpointsIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    public void testGetEmployeesOfProject() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Team-Projekt");
        project.setResponsibleEmployeeId(1L);
        project.setEmployeeIds(Set.of(1L, 2L, 3L));
        ProjectEntity savedProject = projectRepository.save(project);

        mockMvc.perform(get("/projects/{projectId}/employees", savedProject.getId())
                        .with(csrf()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId", is(savedProject.getId().intValue())))
                .andExpect(jsonPath("$.projectName", is("Team-Projekt")))
                .andExpect(jsonPath("$.employeeIds", hasSize(3)));
    }

    @Test
    @WithMockUser
    public void testGetProjectsOfEmployee() throws Exception {
        Long employeeIdToFind = 5L;

        ProjectEntity project1 = new ProjectEntity();
        project1.setName("Projekt A");
        project1.setResponsibleEmployeeId(1L);
        project1.setEmployeeIds(Set.of(1L, employeeIdToFind));
        projectRepository.save(project1);

        ProjectEntity project2 = new ProjectEntity();
        project2.setName("Projekt B");
        project2.setResponsibleEmployeeId(2L);
        project2.setEmployeeIds(Set.of(2L, 4L));
        projectRepository.save(project2);

        ProjectEntity project3 = new ProjectEntity();
        project3.setName("Projekt C");
        project3.setResponsibleEmployeeId(3L);
        project3.setEmployeeIds(Set.of(3L, employeeIdToFind));
        projectRepository.save(project3);

        mockMvc.perform(get("/projects/employees/{employeeId}/projects", employeeIdToFind)
                        .with(csrf()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(is("Projekt A")))
                .andExpect(jsonPath("$[1].name").value(is("Projekt C")));
    }
}