/*
 * File:    RecommendationService.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Suggests products to a user based on what they have bought.
 */
package store.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import store.ds.RecommendationGraph;
import store.model.CartItem;
import store.model.Order;
import store.model.Product;
import store.model.StockStatus;
import store.model.User;

/**
 * Per-user recommendations. Every order adds edges to one shared
 * {@link RecommendationGraph} ("bought together"). A user's suggestions are
 * the graph neighbours of the products that user bought. If the graph has too
 * few, the list is filled with products from the same categories, then any
 * other products in stock.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class RecommendationService {
    /** Default number of recommendations to return. */
    public static final int DEFAULT_LIMIT = 4;

    private final ProductService productService;
    private final RecommendationGraph graph = new RecommendationGraph();
    private final HashMap<String, Set<Integer>> boughtByUser = new HashMap<>();

    /**
     * Creates the service.
     *
     * @param productService used to look up products
     */
    public RecommendationService(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Adds an order to the graph and to the buyer's purchase history.
     *
     * @param order the finished order
     */
    public void recordOrder(Order order) {
        List<Integer> ids = new ArrayList<>();
        for (CartItem item : order.getItems()) {
            ids.add(item.getProductId());
        }
        graph.addOrder(ids);
        boughtByUser.computeIfAbsent(order.getUsername().toLowerCase(), key -> new HashSet<>()).addAll(ids);
    }

    /**
     * Gets recommended products for a user.
     *
     * @param user  the user
     * @param limit maximum number of products
     * @return recommended products, best first; never products already bought or in the cart
     */
    public List<Product> recommendFor(User user, int limit) {
        Set<Integer> bought = boughtByUser.getOrDefault(user.getUsername().toLowerCase(), new HashSet<>());
        Set<Integer> exclude = new HashSet<>(bought);
        for (CartItem item : user.getCart()) {
            exclude.add(item.getProductId());
        }

        List<Product> results = new ArrayList<>();
        for (int id : graph.recommend(bought, exclude, limit)) {
            addIfAvailable(results, exclude, id);
        }

        Set<String> categories = new HashSet<>();
        for (int id : exclude) {
            categories.add(safeCategory(id));
        }
        for (Product product : productService.getAllProducts()) {
            if (results.size() >= limit) {
                break;
            }
            if (categories.contains(product.getCategory())) {
                addIfAvailable(results, exclude, product.getId());
            }
        }
        for (Product product : productService.getAllProducts()) {
            if (results.size() >= limit) {
                break;
            }
            addIfAvailable(results, exclude, product.getId());
        }
        return results;
    }

    /**
     * Gets the underlying graph (used by tests).
     *
     * @return the recommendation graph
     */
    public RecommendationGraph getGraph() {
        return graph;
    }

    /**
     * Adds a product to the results if it still exists (it may have been
     * deleted after it was bought), is in stock, and is not excluded or
     * already in the list.
     *
     * @param results the result list
     * @param exclude IDs to skip (bought or in the cart)
     * @param id      the product ID
     */
    private void addIfAvailable(List<Product> results, Set<Integer> exclude, int id) {
        if (exclude.contains(id) || !productService.hasProduct(id)) {
            return;
        }
        Product product = productService.getProduct(id);
        if (!results.contains(product) && product.getStatus() != StockStatus.OUT_OF_STOCK) {
            results.add(product);
        }
    }

    /**
     * Gets a product's category, or an empty string if it was deleted.
     *
     * @param id the product ID
     * @return the category
     */
    private String safeCategory(int id) {
        if (!productService.hasProduct(id)) {
            return "";
        }
        return productService.getProduct(id).getCategory();
    }
}
