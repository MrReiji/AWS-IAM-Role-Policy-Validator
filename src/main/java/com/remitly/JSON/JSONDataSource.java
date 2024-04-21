package com.remitly.JSON;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Provides methods for sourcing JSON data from various inputs including classpath resources,
 * absolute file paths, and raw JSON strings. This utility ensures that JSON data can be retrieved
 * and parsed regardless of its origin, making it versatile for different runtime environments.
 */
public class JSONDataSource {
    private static final Logger logger = LoggerFactory.getLogger(JSONDataSource.class);

    /**
     * Retrieves a JSON object from a resource located in the classpath.
     *
     * @param resourcePath the relative path within the classpath where the JSON file is located.
     * @return JSONObject parsed from the classpath resource.
     * @throws IllegalArgumentException if the resource cannot be found.
     * @throws RuntimeException if there is an IO error while reading the resource.
     */
    public static JSONObject loadFromResource(String resourcePath) {
        logger.debug("Attempting to load JSON from classpath resource: {}", resourcePath);
        try (InputStream inputStream = JSONDataSource.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.error("Classpath resource not found: {}", resourcePath);
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return new JSONObject(new JSONTokener(inputStream));
        } catch (IOException e) {
            logger.error("IO error on classpath resource: {}", resourcePath, e);
            throw new RuntimeException("IO error on classpath resource: " + resourcePath, e);
        }
    }

    /**
     * Retrieves a JSON object from a file specified by an absolute path.
     *
     * @param filePath the absolute path to the JSON file.
     * @return JSONObject parsed from the file.
     * @throws IllegalArgumentException if the file does not exist.
     * @throws RuntimeException if there is an IO error while reading the file.
     */
    public static JSONObject loadFromAbsolutePath(String filePath) {
        logger.debug("Loading JSON from file: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("File not found: {}", filePath);
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            return new JSONObject(new JSONTokener(inputStream));
        } catch (IOException e) {
            logger.error("IO error on file: {}", filePath, e);
            throw new RuntimeException("IO error on file: " + filePath, e);
        }
    }

    /**
     * Parses a JSON object from a raw JSON string.
     *
     * @param jsonString the string containing the JSON data.
     * @return JSONObject parsed from the string.
     * @throws RuntimeException if the string does not contain valid JSON.
     */
    public static JSONObject loadFromJsonString(String jsonString) {
        logger.debug("Parsing JSON string");
        try {
            return new JSONObject(jsonString);
        } catch (Exception e) {
            logger.error("Invalid JSON string: {}", jsonString, e);
            throw new RuntimeException("Invalid JSON string: " + jsonString, e);
        }
    }
}
