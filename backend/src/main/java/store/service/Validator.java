/*
 * File:    Validator.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Checks and converts input values (names, quantities, prices).
 */
package store.service;

/**
 * Helper methods that check user input and turn text into numbers.
 * Each method throws a {@link ValidationException} with a clear message
 * when the input is invalid.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public final class Validator {

    /** Longest name allowed for a product. */
    public static final int MAX_NAME_LENGTH = 60;

    private Validator() {
    }

    /**
     * Checks that a required text field is not empty.
     *
     * @param value     the text to check
     * @param fieldName the field name used in the error message, for example "Name"
     * @return the text with spaces trimmed from both ends
     */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " can't be empty.");
        }
        return value.trim();
    }

    /**
     * Checks a product name: not empty and not too long.
     *
     * @param name the name to check
     * @return the trimmed name
     */
    public static String validateName(String name) {
        String trimmed = requireText(name, "Name");
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Name must be " + MAX_NAME_LENGTH + " characters or fewer.");
        }
        return trimmed;
    }

    /**
     * Turns text into a quantity. It must be a whole number of 0 or more.
     *
     * @param text the quantity as typed, for example "12"
     * @return the quantity as a number
     */
    public static int parseQuantity(String text) {
        String trimmed = requireText(text, "Quantity");
        int quantity;
        try {
            quantity = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new ValidationException("Quantity must be a whole number.");
        }
        if (quantity < 0) {
            throw new ValidationException("Quantity can't be negative.");
        }
        return quantity;
    }

    /**
     * Turns text into a price. It must be a number of 0 or more.
     *
     * @param text the price as typed, for example "450.50"
     * @return the price as a number
     */
    public static double parsePrice(String text) {
        String trimmed = requireText(text, "Price");
        double price;
        try {
            price = Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            throw new ValidationException("Price must be a number.");
        }
        if (price < 0 || Double.isNaN(price) || Double.isInfinite(price)) {
            throw new ValidationException("Price can't be negative.");
        }
        return price;
    }
}
