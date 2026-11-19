/*
 * File:    ValidationException.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Error thrown when user input is invalid.
 */


package store.service;


/**
 * Thrown when input is invalid, for example an empty name or a negative
 * quantity. The message is shown to the user.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class ValidationException extends RuntimeException {


    /** Version number for Java serialization. */


    private static final long serialVersionUID = 1L;

    /**
     * Creates the error with a message for the user.
     *
     * @param message what was wrong with the input
     */


    public ValidationException
    (
        String message
    ) {
        super(message);
    }
}


