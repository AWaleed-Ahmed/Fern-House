/*
 * File:    CartItem.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: One line in a cart or order: a product ID and how many units.
 */
package store.model;

/**
 * A product ID together with a quantity, used in carts and orders.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class CartItem {
    private int productId;
    private int quantity;

    /**
     * Creates a cart item.
     *
     * @param productId ID of the product
     * @param quantity  number of units
     */
    public CartItem(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Gets the product ID.
     *
     * @return the product ID
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Gets the number of units.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the number of units.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
