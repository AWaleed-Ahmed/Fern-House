/*
 * File:    Order.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: A finished checkout, saved in orders.json.
 */


package store.model;

import java.util.List;


/**
 * A completed order. Orders are used to rebuild the recommendation graph.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class Order {


    private int id;
    private String username;
    private List<CartItem> items;
    private double total;
    private String date;

    /**
     * Creates an order.
     *
     * @param id       unique order ID
     * @param username user who placed the order
     * @param items    products and quantities bought
     * @param total    total price
     * @param date     date and time of the order
     */


    public Order
    (
        int id,
        String username,
        List<CartItem> items,
        double total,
        String date
    ) {
        this.id = id;
        this.username = username;
        this.items = items;
        this.total = total;
        this.date = date;
    }

    /**
     * Gets the order ID.
     *
     * @return the ID
     */


    public int getId() {
        return id;
    }

    /**
     * Gets the username of the buyer.
     *
     * @return the username
     */


    public String getUsername() {
        return username;
    }

    /**
     * Gets the products and quantities bought.
     *
     * @return the items
     */


    public List<CartItem> getItems() {
        return items;
    }

    /**
     * Gets the total price.
     *
     * @return the total
     */


    public double getTotal() {
        return total;
    }

    /**
     * Gets the date and time of the order.
     *
     * @return the date as text
     */


    public String getDate() {
        return date;
    }
}
