/*
 * File:    TrieNode.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: One node (one letter) of the product name trie.
 */
package store.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import store.model.Product;

/**
 * A node of the {@link ProductTrie}. Each node stands for one letter.
 * Children are kept in a TreeMap so they are always in A to Z order.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class TrieNode {
    private final TreeMap<Character, TrieNode> children = new TreeMap<>();
    private final List<Product> products = new ArrayList<>();

    /**
     * Gets the child nodes, keyed by letter.
     *
     * @return the children in A to Z order
     */
    public TreeMap<Character, TrieNode> getChildren() {
        return children;
    }

    /**
     * Gets the products whose full name ends at this node.
     *
     * @return the products stored here
     */
    public List<Product> getProducts() {
        return products;
    }
}
