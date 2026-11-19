/*
 * File:    UserService.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Sign up, log in and log out, with accounts saved in users.json.
 */


package store.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import store.data.JsonStorage;
import store.model.User;


/**
 * Manages accounts. Users are kept in a HashMap by username for quick
 * lookup. Logged-in users get a random session token, kept in a second
 * HashMap from token to username.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class UserService {


    /** File the accounts are saved in. */


    public static final String USERS_FILE = "users.json";

    /** Shortest password allowed. */


    public static final int MIN_PASSWORD_LENGTH = 6;

    /** Shortest username allowed. */


    public static final int MIN_USERNAME_LENGTH = 3;

    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() { }.getType();

    private final JsonStorage storage;
    private final HashMap<String, User> usersByName = new HashMap<>();
    private final HashMap<String, String> sessions = new HashMap<>();

    /**
     * Creates the service and loads accounts from users.json.
     *
     * @param storage the JSON storage to load from and save to
     */


    public UserService
    (
        JsonStorage storage
    ) {
        this.storage = storage;
        List<User> saved = storage.loadList(USERS_FILE, USER_LIST_TYPE);
        for (User user : saved)
        {
            usersByName.put(user.getUsername().toLowerCase(), user);
        }
    }

    /**
     * Creates a customer account. New accounts are never admins; an admin is
     * made by setting "admin": true in users.json.
     *
     * @param username wanted username, letters, numbers and _ only
     * @param password wanted password, at least MIN_PASSWORD_LENGTH characters
     * @return the new user
     */


    public User register
    (
        String username, String password
    ) {
        String cleanName = Validator.requireText(username, "Username");
        if (cleanName.length() < MIN_USERNAME_LENGTH || !cleanName.matches("[A-Za-z0-9_]+"))
        {
            throw new ValidationException("Username must be at least " + MIN_USERNAME_LENGTH
                    + " characters, using letters, numbers or _.");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH)
        {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (usersByName.containsKey(cleanName.toLowerCase()))
        {
            throw new ValidationException("That username is taken. Try another.");
        }
        User user = new User(cleanName, PasswordHasher.hash(password), false);
        usersByName.put(cleanName.toLowerCase(), user);
        save();
        return user;
    }

    /**
     * Checks a username and password and starts a session.
     *
     * @param username the username
     * @param password the plain password
     * @return a session token to send with later requests
     */


    public String login
    (
        String username, String password
    ) {
        User user = username == null ? null : usersByName.get(username.trim().toLowerCase());
        if (user == null || password == null
                || !user.getPasswordHash().equals(PasswordHasher.hash(password)))
        {
            throw new ValidationException("Wrong username or password.");
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, user.getUsername().toLowerCase());
        return token;
    }

    /**
     * Ends a session.
     *
     * @param token the session token to remove
     */


    public void logout
    (
        String token
    ) {
        if (token != null)
        {
            sessions.remove(token);
        }
    }

    /**
     * Finds the user that owns a session token.
     *
     * @param token the session token
     * @return the user, or null if the token is unknown
     */


    public User getUserByToken
    (
        String token
    ) {
        if (token == null)
        {
            return null;
        }
        String name = sessions.get(token);
        return name == null ? null : usersByName.get(name);
    }

    /**
     * Saves all accounts (and their carts) to users.json.
     */


    public void save() {
        storage.saveList(USERS_FILE, new ArrayList<>(usersByName.values()));
    }
}


