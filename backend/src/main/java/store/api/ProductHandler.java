/*
 * File:    ProductHandler.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Public API routes for browsing and searching products.
 */


package store.api;

import java.io.IOException;
import java.util.TreeSet;

import com.sun.net.httpserver.HttpExchange;

import store.model.Product;
import store.service.ProductService;
import store.service.UserService;


/**
 * Handles GET /api/products (with optional ?search= or ?category=),
 * GET /api/products/{id} and GET /api/categories.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class ProductHandler extends ApiHandler {


    private final ProductService productService;

    /**
     * Creates the handler.
     *
     * @param userService    the account service
     * @param productService the inventory service
     */


    public ProductHandler
    (
        UserService userService,
        ProductService productService
    ) {
        super(userService);
        this.productService = productService;
    }

    /**
     * Sends each product request to the right method.
     *
     * @param exchange the HTTP exchange
     * @param method   the HTTP method
     * @param parts    the path parts, for example ["products", "3"]
     * @throws IOException if the response can't be written
     */


    @Override
    protected void route
    (
        HttpExchange exchange,
        String method,
        String[] parts
    ) throws IOException {
        if (!method.equals("GET"))
        {
            sendNotFound(exchange);
            return;
        }
        if (parts[0].equals("categories"))
        {
            TreeSet<String> categories = new TreeSet<>();
            for (Product product : productService.getAllProducts())
            {
                categories.add(product.getCategory());
            }
            sendJson(exchange, 200, categories);
        }
        else if (parts.length == 1)
        {
            String search = queryParam(exchange, "search");
            String category = queryParam(exchange, "category");
            if (search != null && !search.isBlank())
            {
                sendJson(exchange, 200, productService.searchProducts(search));
            }
            else if (category != null && !category.isBlank())
            {
                sendJson(exchange, 200, productService.getByCategory(category));
            }
            else
            {
                sendJson(exchange, 200, productService.getAllProducts());
            }
        }
        else if (parts.length == 2)
        {
            sendJson(exchange, 200, productService.getProduct(parseId(parts[1])));
        }
        else
        {
            sendNotFound(exchange);
        }
    }
}


