package com.banyan.compiler.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class RuleSetSchemaValidator extends SchemaValidatorImpl implements SchemaValidator{

    private static final String resourceName = "/schemas/ruleset.schema.json";
    private static final String exceptionErr = "RuleSet schema not found on classpath";
    private static final String schemaType = "RULE_SET";

    public RuleSetSchemaValidator() {
        super(resourceName, exceptionErr, schemaType);
    }
}
