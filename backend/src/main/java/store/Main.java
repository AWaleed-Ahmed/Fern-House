/*
 * File:    Main.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Entry point: copies the starter data, builds the services and starts the API.
 */


package store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import store.api.ApiServer;
import store.data.JsonStorage;
import store.service.CartService;
import store.service.OrderService;
import store.service.ProductService;
import store.service.RecommendationService;
import store.service.UserService;


/**
 * Starts the store backend.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public final class Main {


    /** Port the API listens on. */


    public static final int DEFAULT_PORT = 8080;

    /** Folder the live JSON data is kept in. */


    public static final String DATA_FOLDER = "data";

    /** Folder with the starter JSON files, copied into the data folder once. */


    public static final String SEED_FOLDER = "seed";

    private Main() {
    }

    /**
     * Starts the server. Run it from the backend folder.
     *
     * @param args optional: the port number
     * @throws IOException if the data can't be copied or the port can't be opened
     */


    public static void main
    (
        String[] args
    ) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        Path dataFolder = Path.of(DATA_FOLDER);
        copySeedData(Path.of(SEED_FOLDER), dataFolder);

        JsonStorage storage = new JsonStorage(dataFolder);
        UserService userService = new UserService(storage);
        ProductService productService = new ProductService(storage);
        CartService cartService = new CartService(productService, userService);
        RecommendationService recommendationService = new RecommendationService(productService);
        OrderService orderService = new OrderService(storage, productService, userService, recommendationService);

        ApiServer server = new ApiServer(port, userService, productService, cartService,
                orderService, recommendationService);
        server.start();
        System.out.println("Store API running at http://localhost:" + server.getPort() + "/api");
    }

    /**
     * Copies each starter file into the data folder if it isn't there yet,
     * so the first run has products and an admin account.
     *
     * @param seedFolder folder with the starter files
     * @param dataFolder folder with the live data
     * @throws IOException if a file can't be copied
     */


    static void copySeedData
    (
        Path seedFolder, Path dataFolder
    ) throws IOException {
        Files.createDirectories(dataFolder);
        if (!Files.isDirectory(seedFolder))
        {
            return;
        }
        try (var files = Files.list(seedFolder))
        {
            for (Path seedFile : (Iterable<Path>) files::iterator)
            {
                Path target = dataFolder.resolve(seedFile.getFileName());
                if (!Files.exists(target))
                {
                    Files.copy(seedFile, target);
                }
            }
        }
    }
}
