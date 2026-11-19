/*
 * File:    User.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: A customer or admin account saved in users.json.
 */


package store.model;

import java.util.LinkedList;


/**
 * A user account. The admin flag in users.json gives a user admin rights.
 * Each user keeps their cart as a linked list.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class User {


    private String username;
    private String passwordHash;
    private boolean admin;
    private LinkedList<CartItem> cart;

    /**
     * Creates a user with an empty cart.
     *
     * @param username     unique username
     * @param passwordHash SHA-256 hash of the password
     * @param admin        true if this user is an admin
     */


    public User
    (
        String username, String passwordHash,
        boolean admin
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.admin = admin;
        this.cart = new LinkedList<>();
    }

    /**
     * Gets the username.
     *
     * @return the username
     */


    public String getUsername() {
        return username;
    }

    /**
     * Gets the stored password hash.
     *
     * @return the SHA-256 hash
     */


    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Checks if this user is an admin.
     *
     * @return true for an admin
     */


    public boolean isAdmin() {
        return admin;
    }

    /**
     * Gets the user's cart. Creates an empty one if it is missing
     * (for example in an older users.json).
     *
     * @return the cart as a linked list
     */


    public LinkedList<CartItem> getCart() {
        if (cart == null)
        {
            cart = new LinkedList<>();
        }
        return cart;
    }
}
