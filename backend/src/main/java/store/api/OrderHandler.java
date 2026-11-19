/*
 * File:    OrderHandler.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: API routes for checkout, order history and recommendations.
 */


package store.api;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import store.model.User;
import store.service.OrderService;
import store.service.RecommendationService;
import store.service.UserService;


/**
 * Handles POST /api/checkout, GET /api/orders and GET /api/recommendations
 * for the logged-in user.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class OrderHandler extends ApiHandler {


    private final OrderService orderService;
    private final RecommendationService recommendationService;

    /**
     * Creates the handler.
     *
     * @param userService           the account service
     * @param orderService          the checkout service
     * @param recommendationService the recommendation service
     */


    public OrderHandler
    (
        UserService userService,
        OrderService orderService,
        RecommendationService recommendationService
    ) {
        super(userService);
        this.orderService = orderService;
        this.recommendationService = recommendationService;
    }

    /**
     * Sends each request to the right method.
     *
     * @param exchange the HTTP exchange
     * @param method   the HTTP method
     * @param parts    the path parts, for example ["checkout"]
     * @throws IOException if the response can't be written
     */


    @Override
    protected void route
    (
        HttpExchange exchange,
        String method,
        String[] parts
    ) throws IOException {
        User user = requireUser(exchange);
        if (parts[0].equals("checkout") && method.equals("POST"))
        {
            sendJson(exchange, 201, orderService.checkout(user));
        }
        else if (parts[0].equals("orders") && method.equals("GET"))
        {
            sendJson(exchange, 200, orderService.getOrdersFor(user));
        }
        else if (parts[0].equals("recommendations") && method.equals("GET"))
        {
            sendJson(exchange, 200,
                    recommendationService.recommendFor(user, RecommendationService.DEFAULT_LIMIT));
        }
        else
        {
            sendNotFound(exchange);
        }
    }
}
