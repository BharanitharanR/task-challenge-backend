package com.banyan.compiler.schema;

import java.util.List;

public class TaskSchemaValidator extends SchemaValidatorImpl implements SchemaValidator {
    private static final String resourceName = "/schemas/Task.schema.json";
    private static  final String exceptionErr = "Task schema not found on classpath";
    private static final String validationErr = "TASK_SCHEMA_PARSE_ERROR:";
    public TaskSchemaValidator() {
        super(resourceName, exceptionErr,validationErr);
    }
}
