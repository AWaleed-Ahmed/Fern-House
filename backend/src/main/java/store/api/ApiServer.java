/*
 * File:    ApiServer.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Starts the HTTP server and connects each URL to its handler.
 */
package store.api;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import store.service.CartService;
import store.service.OrderService;
import store.service.ProductService;
import store.service.RecommendationService;
import store.service.UserService;

/**
 * The REST API server, built on the JDK's own HttpServer. It uses one
 * thread, so requests are handled one at a time and the data structures
 * need no locking.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class ApiServer {
    private final HttpServer server;

    /**
     * Creates the server and registers every route.
     *
     * @param port                  port to listen on, for example 8080
     * @param userService           the account service
     * @param productService        the inventory service
     * @param cartService           the cart service
     * @param orderService          the checkout service
     * @param recommendationService the recommendation service
     * @throws IOException if the port can't be opened
     */
    public ApiServer(int port, UserService userService, ProductService productService,
            CartService cartService, OrderService orderService,
            RecommendationService recommendationService) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        ProductHandler productHandler = new ProductHandler(userService, productService);
        OrderHandler orderHandler = new OrderHandler(userService, orderService, recommendationService);

        server.createContext("/api/auth", new AuthHandler(userService));
        server.createContext("/api/products", productHandler);
        server.createContext("/api/categories", productHandler);
        server.createContext("/api/cart", new CartHandler(userService, cartService, productService));
        server.createContext("/api/checkout", orderHandler);
        server.createContext("/api/orders", orderHandler);
        server.createContext("/api/recommendations", orderHandler);
        server.createContext("/api/admin", new AdminHandler(userService, productService, orderService));
    }

    /**
     * Starts listening for requests.
     */
    public void start() {
        server.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        server.stop(0);
    }

    /**
     * Gets the port the server is listening on.
     *
     * @return the port
     */
    public int getPort() {
        return server.getAddress().getPort();
    }
}
