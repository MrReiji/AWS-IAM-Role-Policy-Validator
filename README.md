# AWS IAM Role Policy Validator

This Java application validates JSON data against the AWS IAM Role policy schema, ensuring it adheres to the AWS official documentation standards and specific project conditions.

## Introduction

This tool validates JSON files defining AWS IAM Roles. It uses a `schema.json` to ensure compliance, particularly verifying that the `Resource` field does not contain a single asterisk ('*'), which could indicate overly broad permissions. If this condition is found, the application returns `false`, otherwise `true`.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Schema](#schema)
- [Features](#features)
- [Dependencies](#dependencies)
- [Running Tests](#running-tests)
- [Contributors](#contributors)
- [License](#license)

## Installation

To set up this project, you will need Java and Maven:

1. Clone the repository:
   ```bash
   git clone https://github.com/MrReiji/AWS-IAM-Role-Policy-Validator.git
   ```
2. Navigate to the project directory:
   ```bash
   cd AWS-IAM-Role-Policy-Validator/
   ```
3. Install the necessary Maven dependencies:
   ```bash
   mvn install
   ```

## Usage

To run the application, you can either specify the path to an input JSON file or directly pass a JSON string as an argument:

### Using a JSON file:

```bash
mvn exec:java -D"exec.mainClass"="com.remitly.application.Main" -D"exec.args"="path\to\your\input.json"
```

### Expected Outputs

When running the application with either a valid or invalid JSON input, you can expect the following outputs in the logs:

- **For a valid JSON input:**
  ```
  [com.remitly.application.Main.main()] INFO com.remitly.schema.SchemaValidator - Schema loaded from /schema.json
  [com.remitly.application.Main.main()] INFO com.remitly.schema.SchemaValidator - Validation successful.
  [com.remitly.application.Main.main()] INFO com.remitly.application.Main - Validation result: Valid
  ```

- **For an invalid JSON input:**
  ```
  [com.remitly.application.Main.main()] INFO com.remitly.schema.SchemaValidator - Schema loaded from /schema.json
  [com.remitly.application.Main.main()] ERROR com.remitly.schema.SchemaValidator - Validation error: #/PolicyDocument/Statement/0/Resource: #: no subschema matched out of the total 2 subschemas
  [com.remitly.application.Main.main()] INFO com.remitly.application.Main - Validation result: Invalid
  ```

## Schema

Below is the JSON schema used by this application to validate AWS IAM Role policies. This schema incorporates checks for the `Resource` field to prevent the assignment of overly broad permissions by ensuring it does not contain a single asterisk ('*').

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "required": ["PolicyName", "PolicyDocument"],
  "additionalProperties": false,
  "properties": {
    "PolicyName": {
      "type": "string",
      "pattern": "[\\w+=,.@-]+",
      "minLength": 1,
      "maxLength": 128
    },
    "PolicyDocument": {
      "type": "object",
      "required": ["Statement"],
      "properties": {
        "Version": {
          "type": "string",
          "enum": ["2012-10-17", "2008-10-17"]
        },
        "Statement": {
          "type": "array",
          "items": {
            "type": "object",
            "required": ["Effect", "Action", "Resource"],
            "properties": {
              "Effect": {
                "type": "string",
                "enum": ["Allow", "Deny"]
              },
              "Action": {
                "anyOf": [
                  {
                    "type": "string",
                    "pattern": "^[a-zA-Z0-9]+:[a-zA-Z0-9]+$"
                  },
                  {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "pattern": "^[a-zA-Z0-9]+:[a-zA-Z0-9]+$"
                    }
                  }
                ]
              },
              "Resource": {
                "anyOf": [
                  {
                    "type": "string",
                    "not": {
                      "enum": ["*"]
                    }
                  },
                  {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "not": {
                        "enum": ["*"]
                      }
                    }
                  }
                ]
              }
            }
          }
        }
      }
    }
  }
}
```

## Features

- **Schema-Based Validation:** Uses a detailed schema derived from AWS documentation to validate JSON structures, focusing on compliance with IAM role policies.
- **Resource Field Verification:** Ensures the `Resource` field does not include a single asterisk ('*'), returning `false` for security risks.
- **Input Flexibility:** Accepts JSON data as a raw string, file path, or classpath resource, managed by `JSONRetrievalCoordinator`.

## Dependencies

- Java JDK 8 or higher.
- Maven.

## Running Tests

The project includes comprehensive tests for the schema validation mechanism:

- **SchemaValidatorTest:** Tests the core functionality of the schema validator against various JSON inputs, ensuring compliance with AWS standards.
- **JSON Retrieval Tests:** Verifies the functionality of the `JSONRetrievalCoordinator`, particularly its ability to handle different types of JSON inputs correctly and robustly.

To run these tests, use the following command:

```bash
mvn test
```

## Contributors

- [Gabriel Rosół](https://github.com/MrReiji)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.

