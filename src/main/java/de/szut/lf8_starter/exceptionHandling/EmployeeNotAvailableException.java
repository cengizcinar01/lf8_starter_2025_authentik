package de.szut.lf8_starter.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an employee cannot be assigned to a project due to a scheduling conflict.
 * Results in a 409 Conflict HTTP status.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmployeeNotAvailableException extends RuntimeException {
    public EmployeeNotAvailableException(String message) {
        super(message);
    }
}