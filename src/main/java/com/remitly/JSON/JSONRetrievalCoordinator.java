package com.remitly.JSON;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinates the retrieval of JSON data based on the type of input provided.
 * This class decides whether to retrieve JSON data from a raw string, a file path, or a classpath resource,
 * utilizing input classification from {@link JSONInputClassifier} and sourcing methods from {@link JSONDataSource}.
 */
public class JSONRetrievalCoordinator {
    private static final Logger logger = LoggerFactory.getLogger(JSONRetrievalCoordinator.class);

    /**
     * Retrieves JSON data for validation or processing based on the input type.
     * This method uses {@link JSONInputClassifier} to determine the nature of the input and
     * {@link JSONDataSource} to fetch the JSON accordingly.
     *
     * @param input The input which could be a JSON string, a file path, or a classpath resource identifier.
     * @return JSONObject parsed and ready for further processing.
     * @throws RuntimeException if the input cannot be successfully parsed or loaded.
     */
    public static JSONObject retrieveJson(String input) {
        if (JSONInputClassifier.isValidJsonString(input)) {
            logger.debug("Input is a valid JSON string, parsing directly.");
            return JSONDataSource.loadFromJsonString(input);
        } else if (JSONInputClassifier.isAbsolutePath(input)) {
            logger.debug("Input is an absolute file path, loading from file.");
            return JSONDataSource.loadFromAbsolutePath(input);
        } else if (JSONInputClassifier.isClasspathResource(input)) {
            logger.debug("Input is identified as a classpath resource, loading accordingly.");
            return JSONDataSource.loadFromResource(input);
        } else {
            logger.error("Input does not match any known JSON source type.");
            throw new RuntimeException("Input type not recognized: " + input);
        }
    }

    /**
     * Specifically retrieves a JSON object from a classpath resource.
     * This method provides a dedicated approach to handle classpath resources distinctly.
     *
     * @param resourcePath The classpath resource path to load the JSON from.
     * @return JSONObject parsed from the classpath resource.
     * @throws RuntimeException if the resource cannot be found or loaded.
     */
    public static JSONObject retrieveJsonFromClasspathResource(String resourcePath) {
        logger.debug("Loading JSON from classpath resource: {}", resourcePath);
        return JSONDataSource.loadFromResource(resourcePath);
    }
}
