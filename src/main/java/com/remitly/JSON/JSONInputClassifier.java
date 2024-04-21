package com.remitly.JSON;

import org.json.JSONObject;
import java.io.File;
import java.net.URL;

/**
 * Classifies JSON input types to determine if the input is a raw JSON string, a file path, or a classpath resource.
 * This helps in ensuring that JSON data is appropriately handled based on its format.
 */
public class JSONInputClassifier {

    /**
     * Determines if the input string is formatted as valid JSON.
     *
     * @param input The input string which might be a JSON string or a file path.
     * @return true if the input is a valid JSON string, otherwise false.
     */
    public static boolean isValidJsonString(String input) {
        try {
            new JSONObject(input);
            return true; // Confirms input is a valid JSON string
        } catch (Exception e) {
            return false; // Indicates input is not a valid JSON string
        }
    }

    /**
     * Checks if the provided path is an absolute file path.
     *
     * @param path The file path to check.
     * @return true if the path is absolute, otherwise false.
     */
    public static boolean isAbsolutePath(String path) {
        return new File(path).isAbsolute();
    }

    /**
     * Determines if the specified path corresponds to a classpath resource.
     *
     * @param resourcePath The path to the resource to check, relative to the classpath.
     * @return true if the resource is found on the classpath, otherwise false.
     */
    public static boolean isClasspathResource(String resourcePath) {
        URL resourceUrl = JSONInputClassifier.class.getResource(resourcePath);
        return resourceUrl != null;
    }
}
