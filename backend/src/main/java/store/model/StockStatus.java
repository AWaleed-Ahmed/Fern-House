/*
 * File:    StockStatus.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Lists the three stock states a product can be in.
 */


package store.model;


/**
 * The stock status of a product: In Stock, Low Stock or Out of Stock.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public enum StockStatus {


    /** The product has enough units. */


    IN_STOCK("In Stock"),

    /** The product is running low. */


    LOW_STOCK("Low Stock"),

    /** The product has no units left. */


    OUT_OF_STOCK("Out of Stock");

    /** Quantity at or below which a product counts as low stock. */


    public static final int LOW_STOCK_THRESHOLD = 5;

    private final String label;

    /**
     * Creates a status with a readable label.
     *
     * @param label the text shown to users, for example "In Stock"
     */


    StockStatus
    (
        String label
    ) {
        this.label = label;
    }

    /**
     * Gets the readable label of this status.
     *
     * @return the label, for example "Low Stock"
     */


    public String getLabel() {
        return label;
    }

    /**
     * Works out the status that matches a quantity.
     *
     * @param quantity the number of units in stock
     * @return OUT_OF_STOCK for 0, LOW_STOCK for 1 to LOW_STOCK_THRESHOLD, otherwise IN_STOCK
     */


    public static StockStatus fromQuantity
    (
        int quantity
    ) {
        if (quantity <= 0)
        {
            return OUT_OF_STOCK;
        }
        if (quantity <= LOW_STOCK_THRESHOLD)
        {
            return LOW_STOCK;
        }
        return IN_STOCK;
    }
}
