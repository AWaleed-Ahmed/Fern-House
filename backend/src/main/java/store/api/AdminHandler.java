/*
 * File:    AdminHandler.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Admin-only API routes for managing the inventory.
 */
package store.api;

import java.io.IOException;
import java.util.Map;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import store.model.StockStatus;
import store.service.OrderService;
import store.service.ProductService;
import store.service.UserService;
import store.service.ValidationException;

/**
 * Handles /api/admin routes. Every route needs a user with "admin": true.
 * POST products adds an item, PUT products/{id} updates it,
 * PUT products/{id}/status sets its status, DELETE products/{id} deletes it,
 * and GET orders lists every order.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class AdminHandler extends ApiHandler {
    private final ProductService productService;
    private final OrderService orderService;

    /**
     * Creates the handler.
     *
     * @param userService    the account service
     * @param productService the inventory service
     * @param orderService   the order service
     */
    public AdminHandler(UserService userService, ProductService productService, OrderService orderService) {
        super(userService);
        this.productService = productService;
        this.orderService = orderService;
    }

    /**
     * Checks the user is an admin and sends the request to the right method.
     *
     * @param exchange the HTTP exchange
     * @param method   the HTTP method
     * @param parts    the path parts, for example ["admin", "products", "3"]
     * @throws IOException if the response can't be written
     */
    @Override
    protected void route(HttpExchange exchange, String method, String[] parts) throws IOException {
        requireAdmin(exchange);
        String section = parts.length > 1 ? parts[1] : "";

        if (section.equals("orders") && method.equals("GET")) {
            sendJson(exchange, 200, orderService.getAllOrders());
        } else if (section.equals("products") && parts.length == 2 && method.equals("POST")) {
            JsonObject body = readBody(exchange);
            sendJson(exchange, 201, productService.addProduct(field(body, "name"), field(body, "category"),
                    field(body, "price"), field(body, "quantity"), field(body, "image")));
        } else if (section.equals("products") && parts.length == 3 && method.equals("PUT")) {
            JsonObject body = readBody(exchange);
            sendJson(exchange, 200, productService.updateProduct(parseId(parts[2]), field(body, "name"),
                    field(body, "category"), field(body, "price"), field(body, "quantity"), field(body, "image")));
        } else if (section.equals("products") && parts.length == 4 && parts[3].equals("status")
                && method.equals("PUT")) {
            JsonObject body = readBody(exchange);
            sendJson(exchange, 200, productService.setStatus(parseId(parts[2]), parseStatus(field(body, "status"))));
        } else if (section.equals("products") && parts.length == 3 && method.equals("DELETE")) {
            productService.deleteProduct(parseId(parts[2]));
            sendJson(exchange, 200, Map.of("ok", true));
        } else {
            sendNotFound(exchange);
        }
    }

    /**
     * Turns text such as "LOW_STOCK" into a {@link StockStatus}.
     *
     * @param text the status name
     * @return the status
     */
    private StockStatus parseStatus(String text) {
        try {
            return StockStatus.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException("Status must be IN_STOCK, LOW_STOCK or OUT_OF_STOCK.");
        }
    }
}
