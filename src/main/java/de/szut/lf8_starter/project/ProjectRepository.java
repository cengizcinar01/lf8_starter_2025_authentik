package de.szut.lf8_starter.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link ProjectEntity}.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    /**
     * Finds all projects where a specific employee is the responsible person.
     * Query is derived from the method name by Spring Data JPA.
     */
    List<ProjectEntity> findByResponsibleEmployeeId(Long employeeId);

    /**
     * Finds all projects that have a specific employee in their team member set.
     * Query is derived from the method name by Spring Data JPA.
     */
    List<ProjectEntity> findByEmployeeIdsContaining(Long employeeId);
}