package com.clark.roper.Dispatch.exception;

/**
 * Thrown when the user is not authorized to perform an action.
 * Maps to HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
