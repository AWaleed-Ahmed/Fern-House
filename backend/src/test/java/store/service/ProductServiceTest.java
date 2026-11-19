/*
 * File:    ProductServiceTest.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Tests the inventory operations, including every case in the OEL brief.
 */


package store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import store.data.JsonStorage;
import store.model.Product;
import store.model.StockStatus;


/**
 * Tests for {@link ProductService}. Each test uses a fresh temporary data folder.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


class ProductServiceTest {


    @TempDir
    Path tempFolder;

    private ProductService service;

    /**
     * Creates a service with an empty inventory before each test.
     */


    @BeforeEach
    void setUp() {
        service = new ProductService(new JsonStorage(tempFolder));
    }

    /** Brief case: add a valid item, so the item is added. */


    @Test
    void addValidItem() {
        Product product = service.addProduct("Green Tea", "Drinks", "300", "12", "tea.jpg");
        assertEquals(1, product.getId());
        assertEquals("Green Tea", service.getProduct(1).getName());
        assertEquals(StockStatus.IN_STOCK, product.getStatus());
    }

    /** Brief case: add an item with an empty name, so a validation message is shown. */


    @Test
    void addItemWithEmptyName() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.addProduct("   ", "Drinks", "300", "12", ""));
        assertEquals("Name can't be empty.", error.getMessage());
    }

    /** Brief case: add an item with a negative quantity, so an error is shown. */


    @Test
    void addItemWithNegativeQuantity() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.addProduct("Green Tea", "Drinks", "300", "-4", ""));
        assertEquals("Quantity can't be negative.", error.getMessage());
    }

    /** Brief case: add an item with a non-numeric quantity, so an error is shown. */


    @Test
    void addItemWithNonNumericQuantity() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.addProduct("Green Tea", "Drinks", "300", "ten", ""));
        assertEquals("Quantity must be a whole number.", error.getMessage());
    }

    /** Brief case: view all items, so all items are displayed in A to Z order. */


    @Test
    void viewAllItems() {
        service.addProduct("Mug", "Kitchen", "500", "5", "");
        service.addProduct("Apron", "Kitchen", "900", "2", "");
        service.addProduct("Kettle", "Kitchen", "2500", "8", "");
        assertEquals(3, service.getAllProducts().size());
        assertEquals("Apron", service.getAllProducts().get(0).getName());
        assertEquals("Mug", service.getAllProducts().get(2).getName());
    }

    /** Brief case: view an empty inventory, so an empty list comes back. */


    @Test
    void viewEmptyInventory() {
        assertTrue(service.getAllProducts().isEmpty());
    }

    /** Brief case: update an existing item, so its details are updated. */


    @Test
    void updateExistingItem() {
        service.addProduct("Mug", "Kitchen", "500", "5", "");
        Product updated = service.updateProduct(1, "Big Mug", "Kitchen", "650", "0", "");
        assertEquals("Big Mug", updated.getName());
        assertEquals(650, updated.getPrice());
        assertEquals(StockStatus.OUT_OF_STOCK, updated.getStatus());
        assertEquals(1, service.searchProducts("big").size());
        assertTrue(service.searchProducts("mug").isEmpty());
    }

    /** Brief case: update a non-existing item, so it is handled with a clear error. */


    @Test
    void updateNonExistingItem() {
        NotFoundException error = assertThrows(NotFoundException.class,
                () -> service.updateProduct(42, "Mug", "Kitchen", "500", "5", ""));
        assertEquals("No product with ID 42.", error.getMessage());
    }

    /** Brief case: delete an existing item, so the item is removed. */


    @Test
    void deleteExistingItem() {
        service.addProduct("Mug", "Kitchen", "500", "5", "");
        service.deleteProduct(1);
        assertTrue(service.getAllProducts().isEmpty());
        assertTrue(service.searchProducts("m").isEmpty());
        assertThrows(NotFoundException.class, () -> service.getProduct(1));
    }

    /** Deleting an ID that doesn't exist gives a clear error. */


    @Test
    void deleteNonExistingItem() {
        assertThrows(NotFoundException.class, () -> service.deleteProduct(7));
    }

    /** The admin can set the status by hand. */


    @Test
    void setStatusByHand() {
        service.addProduct("Mug", "Kitchen", "500", "50", "");
        assertEquals(StockStatus.LOW_STOCK, service.setStatus(1, StockStatus.LOW_STOCK).getStatus());
    }

    /** Status follows the quantity: 0 is out, up to the threshold is low. */


    @Test
    void statusFollowsQuantity() {
        assertEquals(StockStatus.OUT_OF_STOCK, service.addProduct("A", "X", "1", "0", "").getStatus());
        assertEquals(StockStatus.LOW_STOCK, service.addProduct("B", "X", "1",
                String.valueOf(StockStatus.LOW_STOCK_THRESHOLD), "").getStatus());
        assertEquals(StockStatus.IN_STOCK, service.addProduct("C", "X", "1",
                String.valueOf(StockStatus.LOW_STOCK_THRESHOLD + 1), "").getStatus());
    }

    /** Products are saved to JSON and come back after a restart. */


    @Test
    void productsSurviveRestart() {
        service.addProduct("Mug", "Kitchen", "500", "5", "");
        ProductService restarted = new ProductService(new JsonStorage(tempFolder));
        assertEquals("Mug", restarted.getProduct(1).getName());
        assertEquals(1, restarted.searchProducts("mu").size());
    }
}


