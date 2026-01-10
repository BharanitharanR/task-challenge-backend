package com.banyan.compiler.schema;

public class ChallengeSchemaValidator extends SchemaValidatorImpl implements SchemaValidator{

    private static final String resourceName = "/schemas/challenge.schema.json";
    private static final String exceptionErr = "Challenge schema not found on classpath";
    private static final String schemaType = "CHALLENGE";

    public ChallengeSchemaValidator()
    {
        super(resourceName,exceptionErr,schemaType);
    }
}
