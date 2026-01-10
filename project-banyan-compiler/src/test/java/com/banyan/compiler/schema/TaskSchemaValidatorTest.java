package com.banyan.compiler.schema;

import com.banyan.compiler.core.BanyanCompiler;
import com.banyan.compiler.core.CompilationResult;
import com.banyan.compiler.pipeline.EvidenceTypeCompilationPipeline;
import com.banyan.compiler.pipeline.RuleCompilationPipeline;
import com.banyan.compiler.pipeline.RuleSetCompilationPipeline;
import com.banyan.compiler.registry.CompilationPipelineRegistry;
import com.banyan.compiler.testutil.TestResourceLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskSchemaValidatorTest {
    private final TaskSchemaValidator validator =
            new TaskSchemaValidator();

    private static final String VALID_RESOURCE = "task/schema/valid";
    private static final String INVALID_RESOURCE = "task/schema/invalid";


    @Test
    void allValidTaskShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
        }
    }

    @Test
    void allInvalidTaskShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(INVALID_RESOURCE);

        for (String json : jsons) {
            System.out.println(json);
            List<String> errors = validator.validate(json);
            errors.forEach(System.out::println);
            assertFalse(errors.isEmpty(), "Expected errors but got none");
        }
    }

}
