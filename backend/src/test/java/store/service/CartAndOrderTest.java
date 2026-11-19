/*
 * File:    CartAndOrderTest.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Tests accounts, the linked list cart, checkout and recommendations together.
 */


package store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import store.data.JsonStorage;
import store.model.Order;
import store.model.Product;
import store.model.StockStatus;
import store.model.User;


/**
 * Tests for {@link UserService}, {@link CartService}, {@link OrderService}
 * and {@link RecommendationService}.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


class CartAndOrderTest {


    @TempDir
    Path tempFolder;

    private JsonStorage storage;
    private ProductService products;
    private UserService users;
    private CartService carts;
    private RecommendationService recommendations;
    private OrderService orders;
    private User buyer;

    /**
     * Creates the services, four products and one customer before each test.
     */


    @BeforeEach
    void setUp() {
        storage = new JsonStorage(tempFolder);
        products = new ProductService(storage);
        users = new UserService(storage);
        carts = new CartService(products, users);
        recommendations = new RecommendationService(products);
        orders = new OrderService(storage, products, users, recommendations);

        products.addProduct("Coffee Beans", "Coffee", "1200", "10", "");
        products.addProduct("Paper Filters", "Brewing", "300", "3", "");
        products.addProduct("Grinder", "Brewing", "5000", "0", "");
        products.addProduct("Mug", "Kitchen", "500", "20", "");
        buyer = users.register("rehan", "secret1");
    }

    /** Passwords are saved as a SHA-256 hash, never as plain text. */


    @Test
    void passwordIsHashed() {
        assertNotEquals("secret1", buyer.getPasswordHash());
        assertEquals(64, buyer.getPasswordHash().length());
    }

    /** Login works with the right password and fails with a wrong one. */


    @Test
    void loginChecksPassword() {
        String token = users.login("Rehan", "secret1");
        assertEquals("rehan", users.getUserByToken(token).getUsername());
        assertThrows(ValidationException.class, () -> users.login("rehan", "wrong"));
        users.logout(token);
        assertNull(users.getUserByToken(token));
    }

    /** A taken username can't be registered again, and new users are not admins. */


    @Test
    void registerRules() {
        assertThrows(ValidationException.class, () -> users.register("REHAN", "another1"));
        assertThrows(ValidationException.class, () -> users.register("ab", "secret1"));
        assertThrows(ValidationException.class, () -> users.register("newuser", "123"));
        assertFalse(buyer.isAdmin());
    }

    /** Adding the same product twice increases its quantity instead of adding a second node. */


    @Test
    void addingSameProductMergesQuantity() {
        carts.addToCart(buyer, 1, "2");
        carts.addToCart(buyer, 4, "1");
        carts.addToCart(buyer, 1, "3");
        assertEquals(2, buyer.getCart().size());
        assertEquals(5, buyer.getCart().getFirst().getQuantity());
        assertEquals(4, buyer.getCart().getLast().getProductId());
    }

    /** Out of stock products and amounts above the stock are refused. */


    @Test
    void cartChecksStock() {
        assertThrows(ValidationException.class, () -> carts.addToCart(buyer, 3, "1"));
        assertThrows(ValidationException.class, () -> carts.addToCart(buyer, 2, "4"));
        assertThrows(ValidationException.class, () -> carts.addToCart(buyer, 1, "0"));
        assertThrows(NotFoundException.class, () -> carts.addToCart(buyer, 99, "1"));
    }

    /** Items can be updated and removed from the linked list. */


    @Test
    void updateAndRemoveCartItems() {
        carts.addToCart(buyer, 1, "1");
        carts.addToCart(buyer, 4, "1");
        carts.updateItem(buyer, 4, "3");
        assertEquals(3, buyer.getCart().getLast().getQuantity());
        carts.removeItem(buyer, 1);
        assertEquals(1, buyer.getCart().size());
        assertThrows(NotFoundException.class, () -> carts.removeItem(buyer, 1));
        assertEquals(1500, carts.getTotal(buyer));
    }

    /** Checkout reduces stock, updates the status, saves the order and empties the cart. */


    @Test
    void checkoutReducesStock() {
        carts.addToCart(buyer, 1, "2");
        carts.addToCart(buyer, 2, "3");
        Order order = orders.checkout(buyer);

        assertEquals(1200 * 2 + 300 * 3, order.getTotal());
        assertEquals(8, products.getProduct(1).getQuantity());
        assertEquals(StockStatus.OUT_OF_STOCK, products.getProduct(2).getStatus());
        assertTrue(buyer.getCart().isEmpty());
        assertEquals(1, orders.getOrdersFor(buyer).size());
    }

    /** Checking out an empty cart gives a clear error. */


    @Test
    void checkoutEmptyCart() {
        ValidationException error = assertThrows(ValidationException.class, () -> orders.checkout(buyer));
        assertEquals("Your cart is empty.", error.getMessage());
    }

    /** Recommendations come from what other customers bought together, per user. */


    @Test
    void recommendationsUseGraph() {
        User other = users.register("abdullah", "secret2");
        carts.addToCart(other, 1, "1");
        carts.addToCart(other, 4, "1");
        orders.checkout(other);

        carts.addToCart(buyer, 1, "1");
        orders.checkout(buyer);

        List<Product> results = recommendations.recommendFor(buyer, 4);
        assertEquals("Mug", results.get(0).getName());
        for (Product product : results)
        {
            assertNotEquals(1, product.getId());
            assertNotEquals(StockStatus.OUT_OF_STOCK, product.getStatus());
        }
    }

    /** Orders and carts survive a restart, and the graph is rebuilt from orders.json. */


    @Test
    void dataSurvivesRestart() {
        carts.addToCart(buyer, 1, "1");
        carts.addToCart(buyer, 4, "1");
        orders.checkout(buyer);
        carts.addToCart(buyer, 2, "1");

        ProductService products2 = new ProductService(storage);
        UserService users2 = new UserService(storage);
        RecommendationService recs2 = new RecommendationService(products2);
        OrderService orders2 = new OrderService(storage, products2, users2, recs2);

        String token = users2.login("rehan", "secret1");
        User again = users2.getUserByToken(token);
        assertEquals(1, again.getCart().size());
        assertEquals(1, orders2.getOrdersFor(again).size());
        assertEquals(1, recs2.getGraph().getWeight(1, 4));
    }
}


