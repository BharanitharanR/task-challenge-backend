package com.banyan.compiler.schema;

public class RuleSchemaValidator extends SchemaValidatorImpl implements SchemaValidator{


    private static final String resourceName = "/schemas/rule.schema.json";
    private static final String exceptionErr = "Rule schema not found on classpath";
    private static final String schemaType = "RULE";

    public RuleSchemaValidator() {
        super(resourceName, exceptionErr, schemaType);
    }

}
