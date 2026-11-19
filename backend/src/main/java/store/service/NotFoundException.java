/*
 * File:    NotFoundException.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Error thrown when a product, user or cart item does not exist.
 */


package store.service;


/**
 * Thrown when something asked for does not exist, for example updating a
 * product ID that is not in the inventory.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class NotFoundException extends RuntimeException {


    /** Version number for Java serialization. */


    private static final long serialVersionUID = 1L;

    /**
     * Creates the error with a message for the user.
     *
     * @param message what could not be found
     */


    public NotFoundException
    (
        String message
    ) {
        super(message);
    }
}
