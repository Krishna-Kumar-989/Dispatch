package com.clark.roper.Dispatch.exception;

/**
 * Thrown when a requested resource (user, profile, letter, etc.) is not found.
 * Maps to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
