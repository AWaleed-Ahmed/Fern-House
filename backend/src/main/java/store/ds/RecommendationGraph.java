/*
 * File:    RecommendationGraph.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Weighted graph of products that were bought together.
 */
package store.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An undirected weighted graph. Each node is a product ID. An edge between
 * two products means they were bought in the same order, and its weight is
 * how many times that happened. It is stored as an adjacency map:
 * product ID to (neighbour ID to weight).
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */
public class RecommendationGraph {
    private final HashMap<Integer, HashMap<Integer, Integer>> adjacency = new HashMap<>();

    /**
     * Records one order: every pair of products in it gets its edge weight
     * increased by one.
     *
     * @param productIds IDs of the products in the order
     */
    public void addOrder(List<Integer> productIds) {
        for (int first : productIds) {
            for (int second : productIds) {
                if (first != second) {
                    adjacency.computeIfAbsent(first, key -> new HashMap<>())
                            .merge(second, 1, Integer::sum);
                }
            }
        }
    }

    /**
     * Gets how many times two products were bought together.
     *
     * @param first  ID of one product
     * @param second ID of the other product
     * @return the edge weight, or 0 if there is no edge
     */
    public int getWeight(int first, int second) {
        HashMap<Integer, Integer> neighbours = adjacency.get(first);
        if (neighbours == null) {
            return 0;
        }
        return neighbours.getOrDefault(second, 0);
    }

    /**
     * Gets the neighbours of a product and their edge weights.
     *
     * @param productId the product ID
     * @return neighbour ID to weight, empty if the product has no edges
     */
    public Map<Integer, Integer> getNeighbours(int productId) {
        return adjacency.getOrDefault(productId, new HashMap<>());
    }

    /**
     * Recommends products for one user. Every neighbour of a product the user
     * bought gets a score equal to the sum of its edge weights; the highest
     * scores come first.
     *
     * @param boughtIds  IDs of products the user has bought
     * @param excludeIds IDs that must not be recommended (already bought or in the cart)
     * @param limit      maximum number of results
     * @return recommended product IDs, best first
     */
    public List<Integer> recommend(Set<Integer> boughtIds, Set<Integer> excludeIds, int limit) {
        HashMap<Integer, Integer> scores = new HashMap<>();
        for (int bought : boughtIds) {
            for (Map.Entry<Integer, Integer> edge : getNeighbours(bought).entrySet()) {
                if (!excludeIds.contains(edge.getKey())) {
                    scores.merge(edge.getKey(), edge.getValue(), Integer::sum);
                }
            }
        }
        List<Integer> ranked = new ArrayList<>(scores.keySet());
        ranked.sort((a, b) -> scores.get(b) - scores.get(a));
        return ranked.subList(0, Math.min(limit, ranked.size()));
    }
}
