/*
 * File:    ProductService.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Inventory operations: add, view, search, update, delete and set status.
 */


package store.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import store.data.JsonStorage;
import store.ds.ProductCatalog;
import store.model.Product;
import store.model.StockStatus;


/**
 * Manages the inventory. Products are kept in a {@link ProductCatalog}
 * (hash table and trie) and saved to products.json after every change.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class ProductService {


    /** File the products are saved in. */


    public static final String PRODUCTS_FILE = "products.json";

    private static final Type PRODUCT_LIST_TYPE = new TypeToken<List<Product>>() { }.getType();

    private final JsonStorage storage;
    private final ProductCatalog catalog = new ProductCatalog();

    /**
     * Creates the service and loads products from products.json.
     *
     * @param storage the JSON storage to load from and save to
     */


    public ProductService
    (
        JsonStorage storage
    ) {
        this.storage = storage;
        List<Product> saved = storage.loadList(PRODUCTS_FILE, PRODUCT_LIST_TYPE);
        for (Product product : saved)
        {
            catalog.add(product);
        }
    }

    /**
     * Adds a new product after checking its details.
     *
     * @param name     product name, must not be empty
     * @param category product category, must not be empty
     * @param price    price as text, must be a number of 0 or more
     * @param quantity quantity as text, must be a whole number of 0 or more
     * @param image    image file name or URL, may be empty
     * @return the new product with its ID
     */


    public Product addProduct
    (
        String name, String category, String price, String quantity, String image
    ) {
        String cleanName = Validator.validateName(name);
        String cleanCategory = Validator.requireText(category, "Category");
        double cleanPrice = Validator.parsePrice(price);
        int cleanQuantity = Validator.parseQuantity(quantity);

        Product product = new Product(catalog.nextId(), cleanName, cleanCategory,
                cleanPrice, cleanQuantity, image == null ? "" : image.trim());
        catalog.add(product);
        save();
        return product;
    }

    /**
     * Gets all products in A to Z order.
     *
     * @return all products, empty if the inventory is empty
     */


    public List<Product> getAllProducts() {
        return catalog.getAllSorted();
    }

    /**
     * Searches products by the start of their name.
     *
     * @param prefix the start of the name, any case
     * @return matching products in A to Z order
     */


    public List<Product> searchProducts
    (
        String prefix
    ) {
        return catalog.searchByName(prefix == null ? "" : prefix.trim());
    }

    /**
     * Gets all products in one category, in A to Z order.
     *
     * @param category the category name, any case
     * @return the products in that category
     */


    public List<Product> getByCategory
    (
        String category
    ) {
        List<Product> results = new ArrayList<>();
        for (Product product : catalog.getAllSorted())
        {
            if (product.getCategory().equalsIgnoreCase(category))
            {
                results.add(product);
            }
        }
        return results;
    }

    /**
     * Gets one product by ID.
     *
     * @param id the product ID
     * @return the product
     * @throws NotFoundException if there is no product with this ID
     */


    public Product getProduct
    (
        int id
    ) {
        Product product = catalog.findById(id);
        if (product == null)
        {
            throw new NotFoundException("No product with ID " + id + ".");
        }
        return product;
    }

    /**
     * Checks if a product with this ID exists.
     *
     * @param id the product ID
     * @return true if the product is in the inventory
     */


    public boolean hasProduct
    (
        int id
    ) {
        return catalog.findById(id) != null;
    }

    /**
     * Updates the details and quantity of an existing product.
     *
     * @param id       ID of the product to update
     * @param name     new name, must not be empty
     * @param category new category, must not be empty
     * @param price    new price as text
     * @param quantity new quantity as text
     * @param image    new image file name or URL, may be empty
     * @return the updated product
     * @throws NotFoundException if there is no product with this ID
     */


    public Product updateProduct
    (
        int id,
        String name, String category, String price, String quantity, String image
    ) {
        Product product = getProduct(id);
        String cleanName = Validator.validateName(name);
        String cleanCategory = Validator.requireText(category, "Category");
        double cleanPrice = Validator.parsePrice(price);
        int cleanQuantity = Validator.parseQuantity(quantity);

        if (!product.getName().equals(cleanName))
        {
            catalog.rename(product, cleanName);
        }
        product.setCategory(cleanCategory);
        product.setPrice(cleanPrice);
        product.setQuantity(cleanQuantity);
        product.setImage(image == null ? "" : image.trim());
        save();
        return product;
    }

    /**
     * Sets the stock status of a product by hand.
     *
     * @param id     ID of the product
     * @param status the new status
     * @return the updated product
     * @throws NotFoundException if there is no product with this ID
     */


    public Product setStatus
    (
        int id,
        StockStatus status
    ) {
        Product product = getProduct(id);
        product.setStatus(status);
        save();
        return product;
    }

    /**
     * Deletes a product.
     *
     * @param id ID of the product to delete
     * @throws NotFoundException if there is no product with this ID
     */


    public void deleteProduct
    (
        int id
    ) {
        if (catalog.remove(id) == null)
        {
            throw new NotFoundException("No product with ID " + id + ".");
        }
        save();
    }

    /**
     * Takes units out of stock after a purchase.
     *
     * @param id     ID of the product
     * @param amount number of units bought
     */


    public void reduceStock
    (
        int id, int amount
    ) {
        Product product = getProduct(id);
        product.setQuantity(product.getQuantity() - amount);
    }

    /**
     * Saves all products to products.json.
     */


    public void save() {
        storage.saveList(PRODUCTS_FILE, new ArrayList<>(catalog.getAll()));
    }
}


