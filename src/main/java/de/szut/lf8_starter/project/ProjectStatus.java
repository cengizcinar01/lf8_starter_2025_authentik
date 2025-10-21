package de.szut.lf8_starter.project;

/**
 * Represents the lifecycle status of a project.
 */
public enum ProjectStatus {

    /**
     * The project has been planned but has not yet started.
     * This is typically the initial state of a new project.
     */
    PLANNED,

    /**
     * The project is currently in progress.
     */
    RUNNING,

    /**
     * The project has been successfully completed.
     */
    FINISHED,

    /**
     * The project has been terminated before completion.
     */
    CANCELLED
}