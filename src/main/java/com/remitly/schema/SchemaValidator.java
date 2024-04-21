package com.remitly.schema;

import com.remitly.JSON.JSONRetrievalCoordinator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the validation of JSON content against a predefined schema. The class facilitates
 * schema initialization and validation to ensure JSON content adheres to specified formats.
 */
public class SchemaValidator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaValidator.class);
    private Schema schema;

    /**
     * Initializes the JSON schema from a specified classpath location using {@link JSONRetrievalCoordinator}.
     * The loaded schema is then used for validating JSON content.
     *
     * @param schemaFilePath Classpath location of the JSON schema file.
     * @throws RuntimeException if unable to load the schema due to file not being found or parsing errors.
     */
    public void initializeSchema(String schemaFilePath) {
        try {
            JSONObject rawSchema = JSONRetrievalCoordinator.retrieveJsonFromClasspathResource(schemaFilePath);
            this.schema = SchemaLoader.load(rawSchema);
            logger.info("Schema loaded from {}", schemaFilePath);
        } catch (Exception e) {
            logger.error("Failed to load schema from {}: {}", schemaFilePath, e.getMessage(), e);
            throw new RuntimeException("Schema loading failed: " + schemaFilePath, e);
        }
    }

    /**
     * Validates JSON content against the loaded schema. Supports validation of JSON provided as
     * raw strings, file paths, or classpath resources, using {@link JSONRetrievalCoordinator} for data retrieval.
     *
     * @param jsonContent JSON content to validate.
     * @return true if the content is valid under the schema, false otherwise.
     * @throws ValidationException if schema validation fails with detailed error info.
     * @throws IllegalStateException if the schema has not been initialized.
     * @throws RuntimeException for other issues during validation, such as IO errors.
     */
    public boolean validate(String jsonContent) {
        if (this.schema == null) {
            throw new IllegalStateException("Schema not initialized. Initialize schema before validation.");
        }
        logger.debug("Validating content: {}", jsonContent);
        try {
            JSONObject jsonObject = JSONRetrievalCoordinator.retrieveJson(jsonContent);
            this.schema.validate(jsonObject);
            logger.info("Validation successful.");
            return true;
        } catch (ValidationException e) {
            logger.error("Validation error for {}: {}", jsonContent, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during validation: {}", jsonContent, e);
            throw new RuntimeException("Validation failed: " + jsonContent, e);
        }
    }
}
