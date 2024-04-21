package com.remitly.application;

import com.remitly.schema.SchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the application that validates JSON content against a predefined schema.
 * It accepts a single command-line argument that can be a JSON file path, a classpath resource, or a raw JSON string.
 * This flexibility allows the application to validate JSON content from various sources using the {@link SchemaValidator}.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String SCHEMA_FILE_PATH = "/schema.json"; // Path to the JSON schema file for validation

    /**
     * Main method to run the validation process. It initializes the {@link SchemaValidator} to validate the provided JSON input.
     *
     * @param args the command-line arguments, expecting exactly one argument that specifies the JSON source.
     */
    public static void main(String[] args) {
        logger.debug("Starting application with arguments: {}", (Object) args);
        if (args.length < 1) {
            logger.error("Usage: java Main <JSON file path, classpath resource, or raw JSON string>");
            System.exit(1); // Exit code 1 for incorrect usage
        }

        String jsonContent = args[0];

        try {
            SchemaValidator validator = new SchemaValidator();
            validator.initializeSchema(SCHEMA_FILE_PATH); // Initialize the schema before validation
            boolean isValid = validator.validate(jsonContent); // Validate the JSON content
            logger.info("Validation result for {}: {}", jsonContent, isValid ? "Valid" : "Invalid");
            System.exit(isValid ? 0 : 1);
        } catch (Exception e) {
            logger.error("Error during validation: ", e);
            System.exit(2); // Exit code 2 for validation errors
        }
    }
}