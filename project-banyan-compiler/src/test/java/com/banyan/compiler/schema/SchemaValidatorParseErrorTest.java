package com.banyan.compiler.schema;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchemaValidatorParseErrorTest {

    @Test
    void challengeSchema_invalidJson_returnsParseError() {
        SchemaValidator validator = new ChallengeSchemaValidator();
        List<String> errors = validator.validate("{ invalid json ");

        assertFalse(errors.isEmpty());
        assertTrue(errors.getFirst().startsWith("CHALLENGE_PARSE_ERROR"));
    }

    @Test
    void ruleSchema_invalidJson_returnsParseError() {
        SchemaValidator validator = new RuleSchemaValidator();
        List<String> errors = validator.validate("{ invalid json ");

        assertFalse(errors.isEmpty());
        assertTrue(errors.getFirst().startsWith("RULE_PARSE_ERROR"));
    }

    @Test
    void rulesetSchema_invalidJson_returnsParseError() {
        SchemaValidator validator = new RuleSetSchemaValidator();
        List<String> errors = validator.validate("{ invalid json ");

        assertFalse(errors.isEmpty());
        assertTrue(errors.getFirst().startsWith("RULE_SET_PARSE_ERROR"));
    }

    @Test
    void taskSchema_invalidJson_returnsParseError() {
        SchemaValidator validator = new TaskSchemaValidator();
        List<String> errors = validator.validate("{ invalid json ");

        assertFalse(errors.isEmpty());
        assertTrue(errors.getFirst().startsWith("TASK_SCHEMA_PARSE_ERROR"));
    }

    @Test
    void evidenceSchema_invalidJson_returnsParseError() {
        SchemaValidator validator = new EvidenceTypeSchemaValidator();
        List<String> errors = validator.validate("{ invalid json ");

        assertFalse(errors.isEmpty());
        assertTrue(errors.getFirst().startsWith("EVIDENCE_TYPE_PARSE_ERROR"));
    }
}