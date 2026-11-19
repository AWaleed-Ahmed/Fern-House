/*
 * File:    CartService.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Add, update and remove items in a user's cart (a linked list).
 */
package store.service;

import java.util.Iterator;
import java.util.LinkedList;

import store.model.CartItem;
import store.model.Product;
import store.model.StockStatus;
import store.model.User;

/**
 * Cart operations. Each user's cart is a LinkedList of {@link CartItem}:
 * new products are added at the end, and items are found by walking the list.
 * The cart is saved with the user in users.json.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class CartService {
    private final ProductService productService;
    private final UserService userService;

    /**
     * Creates the cart service.
     *
     * @param productService used to check products and stock
     * @param userService    used to save carts
     */
    public CartService(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    /**
     * Adds units of a product to the cart. If the product is already in the
     * cart its quantity goes up, otherwise a new item is added at the end.
     *
     * @param user      the user whose cart it is
     * @param productId ID of the product
     * @param quantity  number of units as text, must be at least 1
     * @return the updated cart
     */
    public LinkedList<CartItem> addToCart(User user, int productId, String quantity) {
        int amount = parseAmount(quantity);
        CartItem existing = findItem(user.getCart(), productId);
        int newTotal = amount + (existing == null ? 0 : existing.getQuantity());
        checkStock(productService.getProduct(productId), newTotal);

        if (existing == null) {
            user.getCart().addLast(new CartItem(productId, amount));
        } else {
            existing.setQuantity(newTotal);
        }
        userService.save();
        return user.getCart();
    }

    /**
     * Changes the quantity of an item already in the cart. A quantity of 0
     * removes the item.
     *
     * @param user      the user whose cart it is
     * @param productId ID of the product
     * @param quantity  new number of units as text
     * @return the updated cart
     */
    public LinkedList<CartItem> updateItem(User user, int productId, String quantity) {
        int amount = Validator.parseQuantity(quantity);
        if (amount == 0) {
            return removeItem(user, productId);
        }
        CartItem existing = findItem(user.getCart(), productId);
        if (existing == null) {
            throw new NotFoundException("That product isn't in your cart.");
        }
        checkStock(productService.getProduct(productId), amount);
        existing.setQuantity(amount);
        userService.save();
        return user.getCart();
    }

    /**
     * Removes a product from the cart by walking the linked list.
     *
     * @param user      the user whose cart it is
     * @param productId ID of the product to remove
     * @return the updated cart
     */
    public LinkedList<CartItem> removeItem(User user, int productId) {
        Iterator<CartItem> iterator = user.getCart().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getProductId() == productId) {
                iterator.remove();
                userService.save();
                return user.getCart();
            }
        }
        throw new NotFoundException("That product isn't in your cart.");
    }

    /**
     * Empties the cart.
     *
     * @param user the user whose cart it is
     */
    public void clearCart(User user) {
        user.getCart().clear();
        userService.save();
    }

    /**
     * Adds up the price of everything in the cart.
     *
     * @param user the user whose cart it is
     * @return the total price
     */
    public double getTotal(User user) {
        double total = 0;
        for (CartItem item : user.getCart()) {
            total += productService.getProduct(item.getProductId()).getPrice() * item.getQuantity();
        }
        return total;
    }

    /**
     * Walks the linked list looking for a product.
     *
     * @param cart      the cart to search
     * @param productId ID of the product
     * @return the cart item, or null if the product is not in the cart
     */
    private CartItem findItem(LinkedList<CartItem> cart, int productId) {
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                return item;
            }
        }
        return null;
    }

    /**
     * Checks that a product can be bought in the wanted amount.
     *
     * @param product the product
     * @param wanted  total number of units wanted
     */
    private void checkStock(Product product, int wanted) {
        if (product.getStatus() == StockStatus.OUT_OF_STOCK || product.getQuantity() == 0) {
            throw new ValidationException(product.getName() + " is out of stock.");
        }
        if (wanted > product.getQuantity()) {
            throw new ValidationException("Only " + product.getQuantity() + " of "
                    + product.getName() + " left.");
        }
    }

    /**
     * Turns text into an amount to add, which must be at least 1.
     *
     * @param quantity the amount as text
     * @return the amount
     */
    private int parseAmount(String quantity) {
        int amount = Validator.parseQuantity(quantity);
        if (amount == 0) {
            throw new ValidationException("Quantity must be at least 1.");
        }
        return amount;
    }
}
