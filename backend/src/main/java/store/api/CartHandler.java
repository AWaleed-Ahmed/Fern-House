/*
 * File:    CartHandler.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: API routes for the logged-in user's cart.
 */
package store.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import store.model.CartItem;
import store.model.Product;
import store.model.User;
import store.service.CartService;
import store.service.ProductService;
import store.service.UserService;

/**
 * Handles /api/cart routes: GET the cart, POST to add an item,
 * PUT /{productId} to change a quantity, DELETE /{productId} to remove
 * one item and DELETE to empty the cart.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class CartHandler extends ApiHandler {
    private final CartService cartService;
    private final ProductService productService;

    /**
     * Creates the handler.
     *
     * @param userService    the account service
     * @param cartService    the cart service
     * @param productService used to add product details to the reply
     */
    public CartHandler(UserService userService, CartService cartService, ProductService productService) {
        super(userService);
        this.cartService = cartService;
        this.productService = productService;
    }

    /**
     * Sends each cart request to the right method.
     *
     * @param exchange the HTTP exchange
     * @param method   the HTTP method
     * @param parts    the path parts, for example ["cart", "4"]
     * @throws IOException if the response can't be written
     */
    @Override
    protected void route(HttpExchange exchange, String method, String[] parts) throws IOException {
        User user = requireUser(exchange);
        if (parts.length == 1 && method.equals("GET")) {
            sendJson(exchange, 200, cartView(user));
        } else if (parts.length == 1 && method.equals("POST")) {
            JsonObject body = readBody(exchange);
            cartService.addToCart(user, parseId(field(body, "productId")), field(body, "quantity"));
            sendJson(exchange, 200, cartView(user));
        } else if (parts.length == 1 && method.equals("DELETE")) {
            cartService.clearCart(user);
            sendJson(exchange, 200, cartView(user));
        } else if (parts.length == 2 && method.equals("PUT")) {
            JsonObject body = readBody(exchange);
            cartService.updateItem(user, parseId(parts[1]), field(body, "quantity"));
            sendJson(exchange, 200, cartView(user));
        } else if (parts.length == 2 && method.equals("DELETE")) {
            cartService.removeItem(user, parseId(parts[1]));
            sendJson(exchange, 200, cartView(user));
        } else {
            sendNotFound(exchange);
        }
    }

    /**
     * Builds the cart reply: every item with its product details, plus the total.
     * Items whose product was deleted are skipped.
     *
     * @param user the user whose cart it is
     * @return a map with items and total
     */
    private Map<String, Object> cartView(User user) {
        List<Map<String, Object>> items = new ArrayList<>();
        double total = 0;
        for (CartItem item : user.getCart()) {
            if (productService.hasProduct(item.getProductId())) {
                Product product = productService.getProduct(item.getProductId());
                Map<String, Object> line = new HashMap<>();
                line.put("product", product);
                line.put("quantity", item.getQuantity());
                line.put("lineTotal", product.getPrice() * item.getQuantity());
                items.add(line);
                total += product.getPrice() * item.getQuantity();
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", total);
        return result;
    }
}
