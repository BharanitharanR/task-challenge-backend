package com.banyan.compiler.semantics;

import com.banyan.compiler.schema.RuleSchemaValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleSemanticValidatorTest {
    private final RuleSemanticValidator validator =
            new RuleSemanticValidator();

    private static final String VALID_RESOURCE = "rule/semantic/valid";
    private static final String INVALID_RESOURCE = "rule/semantic/invalid";

    @Test
    void allValidRulesShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            System.out.println("json:"+json);
            errors.forEach(System.out::println);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
        }
    }

    @Test
    void allInvalidRulesShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(INVALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            System.out.println("json:"+json);
            errors.forEach(System.out::println);
            assertFalse(errors.isEmpty(), "Expected errors but got none");
        }
    }
}
