/*
 * File:    PasswordHasher.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Hashes passwords with SHA-256 so they are never saved as plain text.
 */


package store.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Turns a password into a SHA-256 hash written in hexadecimal.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public final class PasswordHasher {


    private PasswordHasher() {
    }

    /**
     * Hashes a password.
     *
     * @param password the plain password
     * @return the 64 character hexadecimal SHA-256 hash
     */


    public static String hash
    (
        String password
    ) {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes)
            {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
