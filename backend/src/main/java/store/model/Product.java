/*
 * File:    Product.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Represents one product in the store inventory.
 */


package store.model;


/**
 * One product in the inventory, with its stock quantity and status.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class Product {


    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private StockStatus status;
    private String image;

    /**
     * Creates a product. The status is worked out from the quantity.
     *
     * @param id       unique product ID
     * @param name     product name
     * @param category product category
     * @param price    price of one unit
     * @param quantity number of units in stock
     * @param image    image file name or URL
     */


    public Product
    (
        int id,
        String name, String category,
        double price,
        int quantity,
        String image
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.status = StockStatus.fromQuantity(quantity);
        this.image = image;
    }

    /**
     * Gets the product ID.
     *
     * @return the ID
     */


    public int getId() {
        return id;
    }

    /**
     * Sets the product ID.
     *
     * @param id the new ID
     */


    public void setId
    (
        int id
    ) {
        this.id = id;
    }

    /**
     * Gets the product name.
     *
     * @return the name
     */


    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the new name
     */


    public void setName
    (
        String name
    ) {
        this.name = name;
    }

    /**
     * Gets the product category.
     *
     * @return the category
     */


    public String getCategory() {
        return category;
    }

    /**
     * Sets the product category.
     *
     * @param category the new category
     */


    public void setCategory
    (
        String category
    ) {
        this.category = category;
    }

    /**
     * Gets the price of one unit.
     *
     * @return the price
     */


    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of one unit.
     *
     * @param price the new price
     */


    public void setPrice
    (
        double price
    ) {
        this.price = price;
    }

    /**
     * Gets the number of units in stock.
     *
     * @return the quantity
     */


    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity and updates the status to match it.
     *
     * @param quantity the new quantity
     */


    public void setQuantity
    (
        int quantity
    ) {
        this.quantity = quantity;
        this.status = StockStatus.fromQuantity(quantity);
    }

    /**
     * Gets the stock status.
     *
     * @return the status
     */


    public StockStatus getStatus() {
        return status;
    }

    /**
     * Sets the stock status by hand (used by the admin).
     *
     * @param status the new status
     */


    public void setStatus
    (
        StockStatus status
    ) {
        this.status = status;
    }

    /**
     * Gets the image file name or URL.
     *
     * @return the image
     */


    public String getImage() {
        return image;
    }

    /**
     * Sets the image file name or URL.
     *
     * @param image the new image
     */


    public void setImage
    (
        String image
    ) {
        this.image = image;
    }
}
