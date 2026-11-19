/*
 * File:    JsonStorage.java
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Reads and writes lists of objects to JSON files so data survives restarts.
 */


package store.data;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Saves and loads data as JSON files inside one data folder, using Gson.
 *
 * @author Abdullah Waleed Ahmed
 * @author Rehan Haider
 */


public class JsonStorage {


    private final Path dataFolder;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates storage for a data folder. The folder is created if missing.
     *
     * @param dataFolder folder that holds the JSON files
     */


    public JsonStorage
    (
        Path dataFolder
    ) {
        this.dataFolder = dataFolder;
        try
        {
            Files.createDirectories(dataFolder);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Could not create data folder " + dataFolder, e);
        }
    }

    /**
     * Loads a list from a JSON file.
     *
     * @param <T>      type of the items
     * @param fileName file name inside the data folder, for example "products.json"
     * @param listType Gson type of the list, for example {@code new TypeToken<List<Product>>(){}.getType()}
     * @return the loaded items, or an empty list if the file does not exist or is empty
     */


    public <T> List<T> loadList
    (
        String fileName,
        Type listType
    ) {
        Path file = dataFolder.resolve(fileName);
        if (!Files.exists(file))
        {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
        {
            List<T> items = gson.fromJson(reader, listType);
            return items == null ? new ArrayList<>() : items;
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    /**
     * Saves a list to a JSON file, replacing the old file.
     *
     * @param fileName file name inside the data folder
     * @param items    the items to save
     */


    public void saveList
    (
        String fileName,
        List<?> items
    ) {
        Path file = dataFolder.resolve(fileName);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
        {
            gson.toJson(items, writer);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Could not write " + file, e);
        }
    }
}


