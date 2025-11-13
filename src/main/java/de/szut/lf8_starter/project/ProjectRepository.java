package de.szut.lf8_starter.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Efficiently finds all projects where a specific employee is either the responsible person
     * or a team member. This combines both queries into one to avoid duplicate database calls.
     */
    @Query("SELECT DISTINCT p FROM ProjectEntity p " +
           "LEFT JOIN p.employeeIds e " +
           "WHERE p.responsibleEmployeeId = :employeeId OR e = :employeeId")
    List<ProjectEntity> findAllProjectsByEmployeeId(@Param("employeeId") Long employeeId);
}