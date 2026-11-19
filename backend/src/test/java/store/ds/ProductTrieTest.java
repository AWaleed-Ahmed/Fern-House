/*
 * File:    ProductTrieTest.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Tests prefix search and removal in the product name trie.
 */


package store.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import store.model.Product;


/**
 * Tests for {@link ProductTrie}.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


class ProductTrieTest {


    private ProductTrie trie;

    /**
     * Fills a trie with four products before each test.
     */


    @BeforeEach
    void setUp() {
        trie = new ProductTrie();
        trie.insert(new Product(1, "Apple Pie", "Bakery", 400, 5, ""));
        trie.insert(new Product(2, "Apron", "Kitchen", 900, 2, ""));
        trie.insert(new Product(3, "Banana Bread", "Bakery", 350, 9, ""));
        trie.insert(new Product(4, "apple juice", "Drinks", 250, 10, ""));
    }

    /** All products under the "a" node come back in A to Z order, any case. */


    @Test
    void prefixSearchIsSortedAndIgnoresCase() {
        List<Product> results = trie.searchByPrefix("AP");
        assertEquals(3, results.size());
        assertEquals("apple juice", results.get(0).getName());
        assertEquals("Apple Pie", results.get(1).getName());
        assertEquals("Apron", results.get(2).getName());
    }

    /** A prefix with no match gives an empty list. */


    @Test
    void unknownPrefixGivesEmptyList() {
        assertTrue(trie.searchByPrefix("zz").isEmpty());
    }

    /** An empty prefix returns every product. */


    @Test
    void emptyPrefixReturnsAll() {
        assertEquals(4, trie.getAllSorted().size());
    }

    /** Removing a product takes it out of search results. */


    @Test
    void removeProduct() {
        assertTrue(trie.remove("Apron", 2));
        assertEquals(2, trie.searchByPrefix("ap").size());
        assertFalse(trie.remove("Apron", 2));
    }
}


