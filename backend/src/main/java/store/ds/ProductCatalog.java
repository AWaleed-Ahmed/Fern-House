/*
 * File:    ProductCatalog.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Keeps products in a hash table by ID and in a trie by name.
 */


package store.ds;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import store.model.Product;


/**
 * Holds every product in two structures at once:
 * a HashMap (hash buckets) for instant lookup by ID, and a
 * {@link ProductTrie} for searching by name.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class ProductCatalog {


    private final HashMap<Integer, Product> productsById = new HashMap<>();
    private final ProductTrie nameTrie = new ProductTrie();

    /**
     * Adds a product to both the hash table and the trie.
     *
     * @param product the product to add
     */


    public void add
    (
        Product product
    ) {
        productsById.put(product.getId(), product);
        nameTrie.insert(product);
    }

    /**
     * Finds a product by ID in the hash table.
     *
     * @param id the product ID
     * @return the product, or null if there is none with this ID
     */


    public Product findById
    (
        int id
    ) {
        return productsById.get(id);
    }

    /**
     * Searches products by the start of their name using the trie.
     *
     * @param prefix the start of the name, any case
     * @return matching products in A to Z order
     */


    public List<Product> searchByName
    (
        String prefix
    ) {
        return nameTrie.searchByPrefix(prefix);
    }

    /**
     * Gets all products in A to Z order of name.
     *
     * @return all products
     */


    public List<Product> getAllSorted() {
        return nameTrie.getAllSorted();
    }

    /**
     * Gets all products in no particular order (used for saving).
     *
     * @return all products
     */


    public Collection<Product> getAll() {
        return productsById.values();
    }

    /**
     * Renames a product, moving it to its new place in the trie.
     *
     * @param product the product to rename
     * @param newName the new name
     */


    public void rename
    (
        Product product,
        String newName
    ) {
        nameTrie.remove(product.getName(), product.getId());
        product.setName(newName);
        nameTrie.insert(product);
    }

    /**
     * Removes a product from both structures.
     *
     * @param id the product ID
     * @return the removed product, or null if there was none
     */


    public Product remove
    (
        int id
    ) {
        Product product = productsById.remove(id);
        if (product != null)
        {
            nameTrie.remove(product.getName(), id);
        }
        return product;
    }

    /**
     * Works out the next free product ID.
     *
     * @return one more than the largest ID in use, or 1 if empty
     */


    public int nextId() {
        int maxId = 0;
        for (int id : productsById.keySet())
        {
            maxId = Math.max(maxId, id);
        }
        return maxId + 1;
    }

    /**
     * Counts the products.
     *
     * @return the number of products
     */


    public int size() {
        return productsById.size();
    }
}
