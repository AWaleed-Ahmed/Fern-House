/*
 * File:    ApiHandler.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Shared code for every API route: JSON in and out, errors, login checks.
 */
package store.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import store.model.User;
import store.service.NotFoundException;
import store.service.UserService;
import store.service.ValidationException;

/**
 * Base class for the API handlers. It adds CORS headers so the React app can
 * call the API, turns errors into JSON messages with the right status code,
 * and has helpers for reading request bodies and the logged-in user.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public abstract class ApiHandler implements HttpHandler {
    /** Shared JSON converter. */
    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    /** Gives access to the logged-in user. */
    protected final UserService userService;

    /**
     * Creates the handler.
     *
     * @param userService used to find the user from the login token
     */
    protected ApiHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles one request: sets CORS headers, answers browser preflight
     * checks, calls {@link #route} and turns errors into JSON.
     *
     * @param exchange the HTTP request and response
     * @throws IOException if the response can't be written
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }
        try {
            route(exchange, exchange.getRequestMethod(), pathParts(exchange));
        } catch (ValidationException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (NotFoundException e) {
            sendError(exchange, 404, e.getMessage());
        } catch (AuthException e) {
            sendError(exchange, e.getStatusCode(), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            sendError(exchange, 500, "Something went wrong on the server.");
        }
    }

    /**
     * Handles a request for this route. Each handler decides what to do from
     * the method and the path parts.
     *
     * @param exchange the HTTP request and response
     * @param method   the HTTP method, for example "GET"
     * @param parts    the path split on "/", without "api", for example ["products", "3"]
     * @throws IOException if the response can't be written
     */
    protected abstract void route(HttpExchange exchange, String method, String[] parts) throws IOException;

    /**
     * Sends an object as JSON.
     *
     * @param exchange the HTTP exchange
     * @param status   HTTP status code
     * @param body     the object to turn into JSON
     * @throws IOException if the response can't be written
     */
    protected void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    /**
     * Sends an error message as JSON: {"error": "..."}.
     *
     * @param exchange the HTTP exchange
     * @param status   HTTP status code
     * @param message  the message for the user
     * @throws IOException if the response can't be written
     */
    protected void sendError(HttpExchange exchange, int status, String message) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        sendJson(exchange, status, body);
    }

    /**
     * Sends a 404 for a path this handler doesn't know.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if the response can't be written
     */
    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendError(exchange, 404, "Unknown API path.");
    }

    /**
     * Reads the request body as a JSON object.
     *
     * @param exchange the HTTP exchange
     * @return the JSON object, empty if there was no body
     * @throws IOException if the body can't be read
     */
    protected JsonObject readBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            if (text.isBlank()) {
                return new JsonObject();
            }
            JsonElement element = JsonParser.parseString(text);
            if (!element.isJsonObject()) {
                throw new ValidationException("The request body must be a JSON object.");
            }
            return element.getAsJsonObject();
        } catch (com.google.gson.JsonParseException e) {
            throw new ValidationException("The request body isn't valid JSON.");
        }
    }

    /**
     * Reads one field of a JSON body as text, so numbers and strings can be
     * checked by the same validation code.
     *
     * @param body the JSON body
     * @param key  the field name
     * @return the value as text, or null if missing
     */
    protected String field(JsonObject body, String key) {
        JsonElement value = body.get(key);
        if (value == null || value.isJsonNull() || !value.isJsonPrimitive()) {
            return null;
        }
        return value.getAsString();
    }

    /**
     * Reads a query parameter from the URL, for example search in ?search=ap.
     *
     * @param exchange the HTTP exchange
     * @param key      the parameter name
     * @return the decoded value, or null if missing
     */
    protected String queryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue[0].equals(key)) {
                return keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
            }
        }
        return null;
    }

    /**
     * Turns a path part into a number, for example the "3" in /products/3.
     *
     * @param text the path part
     * @return the number
     */
    protected int parseId(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID must be a number.");
        }
    }

    /**
     * Gets the logged-in user from the "Authorization: Bearer token" header.
     *
     * @param exchange the HTTP exchange
     * @return the user
     * @throws AuthException with 401 if there is no valid login
     */
    protected User requireUser(HttpExchange exchange) {
        User user = userService.getUserByToken(token(exchange));
        if (user == null) {
            throw new AuthException(401, "Log in to continue.");
        }
        return user;
    }

    /**
     * Gets the logged-in user and checks they are an admin.
     *
     * @param exchange the HTTP exchange
     * @return the admin user
     * @throws AuthException with 401 if not logged in, 403 if not an admin
     */
    protected User requireAdmin(HttpExchange exchange) {
        User user = requireUser(exchange);
        if (!user.isAdmin()) {
            throw new AuthException(403, "Only admins can do that.");
        }
        return user;
    }

    /**
     * Reads the login token from the Authorization header.
     *
     * @param exchange the HTTP exchange
     * @return the token, or null if missing
     */
    protected String token(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring("Bearer ".length()).trim();
    }

    /**
     * Splits the URL path into parts, dropping empty parts and the leading "api".
     *
     * @param exchange the HTTP exchange
     * @return the parts, for example ["cart", "4"] for /api/cart/4
     */
    private String[] pathParts(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String trimmed = path.replaceAll("^/+|/+$", "");
        if (trimmed.startsWith("api/")) {
            trimmed = trimmed.substring(4);
        } else if (trimmed.equals("api")) {
            trimmed = "";
        }
        return trimmed.isEmpty() ? new String[0] : trimmed.split("/+");
    }
}
