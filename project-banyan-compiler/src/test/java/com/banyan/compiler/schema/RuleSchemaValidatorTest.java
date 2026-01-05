package com.banyan.compiler.schema;

import com.banyan.compiler.testutil.JsonAssert;
import com.banyan.compiler.testutil.TestResourceLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleSchemaValidatorTest {
    private final RuleSchemaValidator validator =
            new RuleSchemaValidator();

    private static final String VALID_RESOURCE = "rule/schema/valid";
    private static final String INVALID_RESOURCE = "rule/schema/invalid";

    @Test
    void allValidRulesShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
        }
    }

    @Test
    void allInvalidRulesShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(INVALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            // errors.forEach(System.out::println);
            assertFalse(errors.isEmpty(), "Expected errors but got none");
        }
    }
}
