/*
 * File:    ProductTrie.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Prefix tree (trie) for searching products by name.
 */


package store.ds;

import java.util.ArrayList;
import java.util.List;

import store.model.Product;


/**
 * A trie (prefix tree) of product names. Every node is one letter, so all
 * products starting with "a" sit under the "a" node, all starting with "ap"
 * under "a" then "p", and so on. Searching costs the length of the typed text,
 * not the number of products. Names are stored in lower case.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class ProductTrie {


    private final TrieNode root = new TrieNode();

    /**
     * Adds a product under its name.
     *
     * @param product the product to add
     */


    public void insert
    (
        Product product
    ) {
        TrieNode current = root;
        for (char letter : product.getName().toLowerCase().toCharArray())
        {
            current = current.getChildren().computeIfAbsent(letter, key -> new TrieNode());
        }
        current.getProducts().add(product);
    }

    /**
     * Removes a product that was stored under the given name.
     *
     * @param name      the name the product was stored under
     * @param productId ID of the product to remove
     * @return true if the product was found and removed
     */


    public boolean remove
    (
        String name,
        int productId
    ) {
        TrieNode node = findNode(name.toLowerCase());
        if (node == null)
        {
            return false;
        }
        return node.getProducts().removeIf(product -> product.getId() == productId);
    }

    /**
     * Finds every product whose name starts with the prefix, in A to Z order.
     *
     * @param prefix the start of the name, any case; an empty prefix returns all products
     * @return the matching products, empty if none match
     */


    public List<Product> searchByPrefix
    (
        String prefix
    ) {
        List<Product> results = new ArrayList<>();
        TrieNode start = findNode(prefix.toLowerCase());
        if (start != null)
        {
            collect(start, results);
        }
        return results;
    }

    /**
     * Gets every product in A to Z order of name.
     *
     * @return all products
     */


    public List<Product> getAllSorted() {
        return searchByPrefix("");
    }

    /**
     * Walks down the trie one letter at a time.
     *
     * @param text the letters to follow, already lower case
     * @return the node at the end of the text, or null if the path does not exist
     */


    private TrieNode findNode
    (
        String text
    ) {
        TrieNode current = root;
        for (char letter : text.toCharArray())
        {
            current = current.getChildren().get(letter);
            if (current == null)
            {
                return null;
            }
        }
        return current;
    }

    /**
     * Collects all products under a node with a depth-first walk.
     * Children come out of the TreeMap in A to Z order, so results are sorted.
     *
     * @param node    the node to start from
     * @param results the list the products are added to
     */


    private void collect
    (
        TrieNode node,
        List<Product> results
    ) {
        results.addAll(node.getProducts());
        for (TrieNode child : node.getChildren().values())
        {
            collect(child, results);
        }
    }
}
