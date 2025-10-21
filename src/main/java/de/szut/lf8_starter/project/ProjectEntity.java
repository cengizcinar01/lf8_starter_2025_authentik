package de.szut.lf8_starter.project;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

/**
 * Represents a project in the database.
 * This entity contains all core information about a project.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity {

    /**
     * The unique identifier for the project.
     * Generated automatically by the database (identity strategy).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name or title of the project.
     * This field cannot be null.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * A detailed description of the project's goals and scope.
     */
    @Column(name = "description")
    private String description;

    /**
     * The ID of the customer who commissioned the project.
     * This will later be used to link to the customer service.
     */
    @Column(name = "customer_id")
    private Long customerId;

    /**
     * The ID of the employee who is responsible for the project.
     * This field cannot be null and is validated against the employee service.
     */
    @Column(name = "responsible_employee_id", nullable = false)
    private Long responsibleEmployeeId;

    /**
     * The official start date of the project.
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * The planned end date of the project.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * The current status of the project (e.g., PLANNED, RUNNING).
     * Stored as a string in the database for better readability.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status;

    /**
     * A set of employee IDs who are assigned to this project.
     * Stored in a separate table 'project_employees' and loaded eagerly.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_employees", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "employee_id")
    private Set<Long> employeeIds;
}