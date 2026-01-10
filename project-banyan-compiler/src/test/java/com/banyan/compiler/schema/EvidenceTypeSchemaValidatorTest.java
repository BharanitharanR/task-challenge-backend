package com.banyan.compiler.schema;

import com.banyan.compiler.testutil.JsonAssert;
import com.banyan.compiler.testutil.TestResourceLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvidenceTypeSchemaValidatorTest {

    private final EvidenceTypeSchemaValidator validator =
            new EvidenceTypeSchemaValidator();

    private static final String VALID_RESOURCE = "evidence-type/schema/valid";
    private static final String INVALID_RESOURCE = "evidence-type/schema/invalid";

    @Test
    void allValidEvidenceTypesShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
        }
    }

    @Test
    void allInvalidEvidenceTypesShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(INVALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            System.out.println(json);
            errors.forEach(System.out::println);
            assertFalse(errors.isEmpty(), "Expected errors but got none");
        }
    }

    @Test
    void validateSpecificFields() {
        String json = TestResourceLoader
                .loadJsonFiles(VALID_RESOURCE)
                .getFirst();

        JsonAssert assertJson = new JsonAssert(json);

        assertJson.assertFieldExists("/spec/fields/0/name");
        assertJson.assertBooleanTrue("/spec/fields/0/required");
        assertJson.assertEquals("/kind", "EvidenceType");
    }

}
