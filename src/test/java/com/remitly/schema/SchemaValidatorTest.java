package com.remitly.schema;

import com.remitly.JSON.JSONRetrievalCoordinator;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests for SchemaValidator focusing on JSON input validation against AWS IAM Role Policy schema,
 * particularly testing various configurations of the "Resource" field and file-based input validation.
 */
public class SchemaValidatorTest {
    private SchemaValidator validator;
    private MockedStatic<JSONRetrievalCoordinator> mockedStatic;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        validator = new SchemaValidator();
        validator.initializeSchema("/schema.json");  // Assuming the schema is correctly set up in the resources directory

        mockedStatic = mockStatic(JSONRetrievalCoordinator.class);
    }

    @After
    public void tearDown() {
        mockedStatic.close();
    }

    /**
     * Test to ensure that a Resource field containing only a wildcard "*" fails validation according to schema restrictions.
     */
    @Test
    public void testResourceWithOnlyWildcard() {
        String json = "{\"PolicyName\": \"OnlyWildcard\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Sid\": \"WildcardOnly\", \"Effect\": \"Allow\", \"Action\": [\"iam:ListAllMyBuckets\"], \"Resource\": \"*\"}]}}";
        when(JSONRetrievalCoordinator.retrieveJson(anyString())).thenReturn(new JSONObject(json));
        assertFalse("Resource with only '*' should fail validation", validator.validate(json));
    }

    /**
     * Test to verify that a Resource field containing a wildcard "*" with trailing whitespace is handled correctly and should pass.
     * According to the revised requirement, only a single asterisk '*' without any modification or whitespace should fail.
     */
    @Test
    public void testResourceWithWildcardAndSpace() {
        String json = "{\"PolicyName\": \"WildcardWithSpace\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Sid\": \"WildcardSpace\", \"Effect\": \"Allow\", \"Action\": [\"iam:ListAllMyBuckets\"], \"Resource\": \"* \"}]}}";
        when(JSONRetrievalCoordinator.retrieveJson(anyString())).thenReturn(new JSONObject(json));
        assertTrue("Resource with '* ' should pass validation as it does not strictly contain only '*'", validator.validate(json));
    }

    /**
     * Test to ensure that malformed Resource entries such as "**" are accepted by the schema since the failure condition is strictly for a single asterisk.
     */
    @Test
    public void testResourceWithDoubleWildcards() {
        String json = "{\"PolicyName\": \"DoubleWildcards\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Sid\": \"DoubleWildcard\", \"Effect\": \"Allow\", \"Action\": [\"iam:ListAllMyBuckets\"], \"Resource\": \"**\"}]}}";
        when(JSONRetrievalCoordinator.retrieveJson(anyString())).thenReturn(new JSONObject(json));
        assertTrue("Resource with '**' should pass validation as it does not strictly contain only '*'", validator.validate(json));
    }

    /**
     * Test to confirm that a policy JSON without a Resource field entirely fails validation, as Resource is required by schema.
     */
    @Test
    public void testPolicyWithoutResource() {
        String json = "{\"PolicyName\": \"NoResource\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Sid\": \"NoResource\", \"Effect\": \"Allow\", \"Action\": [\"iam:ListAllMyBuckets\"]}]}}";
        when(JSONRetrievalCoordinator.retrieveJson(anyString())).thenReturn(new JSONObject(json));
        assertFalse("Policy without Resource should fail validation", validator.validate(json));
    }

    /**
     * Validates JSON content from a valid policy file, ensuring that file-based inputs are correctly handled.
     */
    @Test
    public void testValidPolicyLoadedFromFile() throws Exception {
        Path path = Paths.get(getClass().getResource("/test_valid_policy.json").toURI());
        String json = new String(Files.readAllBytes(path));
        when(JSONRetrievalCoordinator.retrieveJson(anyString())).thenReturn(new JSONObject(json));
        assertTrue("Valid policy file should pass validation", validator.validate(json));
    }

    /**
     * Validates JSON content from an invalid policy file, checking for proper handling of incorrect data.
     */
    @Test
    public void testInvalidPolicyLoadedFromFile() throws Exception {
        Path path = Paths.get(getClass().getResource("/test_invalid_policy.json").toURI());
        String json = new String(Files.readAllBytes(path));
        when(JSONRetrievalCoordinator.retrieveJson(anyString())).thenReturn(new JSONObject(json));
        assertFalse("Invalid policy file should fail validation", validator.validate(json));
    }
}
