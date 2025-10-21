package de.szut.lf8_starter.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ProjectEntity.
 * Handles all database operations for projects.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}