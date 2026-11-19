/*
 * File:    RecommendationGraphTest.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Tests edge weights and ranking in the recommendation graph.
 */


package store.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;


/**
 * Tests for {@link RecommendationGraph}.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


class RecommendationGraphTest {


    /** Products bought together get an edge in both directions. */


    @Test
    void orderCreatesEdgesBothWays() {
        RecommendationGraph graph = new RecommendationGraph();
        graph.addOrder(List.of(1, 2));
        assertEquals(1, graph.getWeight(1, 2));
        assertEquals(1, graph.getWeight(2, 1));
        assertEquals(0, graph.getWeight(1, 3));
    }

    /** The product bought together most often is recommended first. */


    @Test
    void heaviestNeighbourComesFirst() {
        RecommendationGraph graph = new RecommendationGraph();
        graph.addOrder(List.of(1, 2));
        graph.addOrder(List.of(1, 3));
        graph.addOrder(List.of(1, 3));
        List<Integer> results = graph.recommend(Set.of(1), Set.of(1), 5);
        assertEquals(List.of(3, 2), results);
    }

    /** Excluded products are never recommended, and the limit is respected. */


    @Test
    void excludeAndLimit() {
        RecommendationGraph graph = new RecommendationGraph();
        graph.addOrder(List.of(1, 2, 3, 4));
        List<Integer> results = graph.recommend(Set.of(1), Set.of(1, 2), 1);
        assertEquals(1, results.size());
        assertTrue(results.get(0) == 3 || results.get(0) == 4);
    }
}


