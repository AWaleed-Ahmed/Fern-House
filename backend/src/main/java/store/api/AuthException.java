/*
 * File:    AuthException.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Error thrown when a request is not logged in or not allowed.
 */
package store.api;

/**
 * Thrown when a request has no valid login (HTTP 401) or the user is not
 * allowed to do something, such as a customer using admin actions (HTTP 403).
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;

    /**
     * Creates the error.
     *
     * @param statusCode HTTP status, 401 or 403
     * @param message    what went wrong
     */
    public AuthException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Gets the HTTP status code.
     *
     * @return 401 or 403
     */
    public int getStatusCode() {
        return statusCode;
    }
}
