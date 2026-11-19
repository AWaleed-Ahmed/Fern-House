/*
 * File:    OrderService.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Checkout: turns a cart into an order and reduces stock.
 */


package store.service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import store.data.JsonStorage;
import store.model.CartItem;
import store.model.Order;
import store.model.Product;
import store.model.StockStatus;
import store.model.User;


/**
 * Handles checkout and order history. Orders are saved in orders.json and
 * fed into the recommendation graph, both at startup and after each checkout.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class OrderService {


    /** File the orders are saved in. */


    public static final String ORDERS_FILE = "orders.json";

    private static final Type ORDER_LIST_TYPE = new TypeToken<List<Order>>() { }.getType();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JsonStorage storage;
    private final ProductService productService;
    private final UserService userService;
    private final RecommendationService recommendationService;
    private final List<Order> orders;

    /**
     * Creates the service, loads orders.json and rebuilds the graph from it.
     *
     * @param storage               the JSON storage
     * @param productService        used to check and reduce stock
     * @param userService           used to save the emptied cart
     * @param recommendationService receives every order
     */


    public OrderService
    (
        JsonStorage storage,
        ProductService productService,
        UserService userService,
        RecommendationService recommendationService
    ) {
        this.storage = storage;
        this.productService = productService;
        this.userService = userService;
        this.recommendationService = recommendationService;
        this.orders = storage.loadList(ORDERS_FILE, ORDER_LIST_TYPE);
        for (Order order : orders)
        {
            recommendationService.recordOrder(order);
        }
    }

    /**
     * Buys everything in the user's cart. Stock is checked for every item
     * first, so either the whole order goes through or nothing changes.
     *
     * @param user the user checking out
     * @return the new order
     */


    public Order checkout
    (
        User user
    ) {
        if (user.getCart().isEmpty())
        {
            throw new ValidationException("Your cart is empty.");
        }
        double total = 0;
        for (CartItem item : user.getCart())
        {
            Product product = productService.getProduct(item.getProductId());
            if (product.getStatus() == StockStatus.OUT_OF_STOCK || item.getQuantity() > product.getQuantity())
            {
                throw new ValidationException("Not enough stock for " + product.getName() + ".");
            }
            total += product.getPrice() * item.getQuantity();
        }

        List<CartItem> items = new ArrayList<>();
        for (CartItem item : user.getCart())
        {
            productService.reduceStock(item.getProductId(), item.getQuantity());
            items.add(new CartItem(item.getProductId(), item.getQuantity()));
        }
        Order order = new Order(nextId(), user.getUsername(), items, total,
                LocalDateTime.now().format(DATE_FORMAT));
        orders.add(order);
        recommendationService.recordOrder(order);

        user.getCart().clear();
        productService.save();
        userService.save();
        storage.saveList(ORDERS_FILE, orders);
        return order;
    }

    /**
     * Gets the orders of one user, newest first.
     *
     * @param user the user
     * @return the user's orders
     */


    public List<Order> getOrdersFor
    (
        User user
    ) {
        List<Order> results = new ArrayList<>();
        for (int i = orders.size() - 1; i >= 0; i--)
        {
            if (orders.get(i).getUsername().equalsIgnoreCase(user.getUsername()))
            {
                results.add(orders.get(i));
            }
        }
        return results;
    }

    /**
     * Gets every order, newest first (for the admin).
     *
     * @return all orders
     */


    public List<Order> getAllOrders() {
        List<Order> results = new ArrayList<>(orders);
        Collections.reverse(results);
        return results;
    }

    /**
     * Works out the next free order ID.
     *
     * @return one more than the largest order ID, or 1 if there are none
     */


    private int nextId() {
        int maxId = 0;
        for (Order order : orders)
        {
            maxId = Math.max(maxId, order.getId());
        }
        return maxId + 1;
    }
}


