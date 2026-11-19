/*
 * File:    AuthHandler.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: API routes for sign up, log in, log out and the current user.
 */


package store.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import store.model.User;
import store.service.UserService;


/**
 * Handles /api/auth routes:
 * POST register, POST login, POST logout and GET me.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class AuthHandler extends ApiHandler {


    /**
     * Creates the handler.
     *
     * @param userService the account service
     */


    public AuthHandler
    (
        UserService userService
    ) {
        super(userService);
    }

    /**
     * Sends each auth request to the right method.
     *
     * @param exchange the HTTP exchange
     * @param method   the HTTP method
     * @param parts    the path parts, for example ["auth", "login"]
     * @throws IOException if the response can't be written
     */


    @Override
    protected void route
    (
        HttpExchange exchange,
        String method,
        String[] parts
    ) throws IOException {
        String action = parts.length > 1 ? parts[1] : "";
        if (method.equals("POST") && action.equals("register"))
        {
            JsonObject body = readBody(exchange);
            userService.register(field(body, "username"), field(body, "password"));
            String token = userService.login(field(body, "username"), field(body, "password"));
            sendJson(exchange, 201, session(token, userService.getUserByToken(token)));
        }
        else if (method.equals("POST") && action.equals("login"))
        {
            JsonObject body = readBody(exchange);
            String token = userService.login(field(body, "username"), field(body, "password"));
            sendJson(exchange, 200, session(token, userService.getUserByToken(token)));
        }
        else if (method.equals("POST") && action.equals("logout"))
        {
            userService.logout(token(exchange));
            sendJson(exchange, 200, Map.of("ok", true));
        }
        else if (method.equals("GET") && action.equals("me"))
        {
            sendJson(exchange, 200, publicUser(requireUser(exchange)));
        }
        else
        {
            sendNotFound(exchange);
        }
    }

    /**
     * Builds the login reply: the token and the public user details.
     *
     * @param token the session token
     * @param user  the logged-in user
     * @return a map with token and user
     */


    private Map<String, Object> session
    (
        String token,
        User user
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", publicUser(user));
        return result;
    }

    /**
     * Gets the user details that are safe to send (never the password hash).
     *
     * @param user the user
     * @return a map with username and admin
     */


    static Map<String, Object> publicUser
    (
        User user
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("admin", user.isAdmin());
        return result;
    }
}


