package com.banyan.compiler.schema;

public class EvidenceTypeSchemaValidator extends SchemaValidatorImpl implements SchemaValidator {

    private static final String resourceName = "/schemas/evidence-type.schema.json";
    private static final String exceptionErr = "EvidenceType schema not found on classpath";
    private static final String schemaType = "EVIDENCE_TYPE";

    public EvidenceTypeSchemaValidator() {
        super(resourceName, exceptionErr, schemaType);
    }

}
