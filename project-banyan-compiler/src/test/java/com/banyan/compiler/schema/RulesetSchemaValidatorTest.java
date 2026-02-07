package com.banyan.compiler.schema;

import com.banyan.compiler.pipeline.EvidenceTypeCompilationPipeline;
import com.banyan.compiler.pipeline.RuleCompilationPipeline;
import com.banyan.compiler.pipeline.RuleSetCompilationPipeline;
import com.banyan.compiler.registry.CompilationPipelineRegistry;
import com.banyan.compiler.core.BanyanCompiler;
import com.banyan.compiler.core.CompilationResult;
import org.junit.jupiter.api.BeforeEach;
import com.banyan.compiler.testutil.TestResourceLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesetSchemaValidatorTest {
    private final RuleSetSchemaValidator validator =
            new RuleSetSchemaValidator();

    private static final String VALID_RESOURCE = "ruleset/schema/valid";
    private static final String INVALID_RESOURCE = "ruleset/schema/invalid";

    public CompilationPipelineRegistry registry = new CompilationPipelineRegistry();
    public BanyanCompiler compiler;

    @BeforeEach
    void setupCompiler() {
        this.registry = new CompilationPipelineRegistry();
        this.registry.register("Rule", new RuleCompilationPipeline());
        this.registry.register("Ruleset", new RuleSetCompilationPipeline());
        this.registry.register("EvidenceType", new EvidenceTypeCompilationPipeline());
        this.compiler = new BanyanCompiler(this.registry);
    }

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
            System.out.println(json);
            List<String> errors = validator.validate(json);
            errors.forEach(System.out::println);
            assertFalse(errors.isEmpty(), "Expected errors but got none");
        }
    }

    @Test
    void allCompilerCallShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);

        for (String json : jsons) {
            compileWithDependencies(json);
        }
    }

    @Test
    void allCompilerCallShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(INVALID_RESOURCE);
        for (String json : jsons) {
            CompilationResult result = this.compiler.compile(json);
            result.getErrors().forEach(System.out::println);
            assertTrue(!result.isSuccess());
        }
    }

    private void compileWithDependencies(String rulesetJson) {
        List<String> evidenceJsons = TestResourceLoader.loadJsonFiles("evidence-type/schema/valid");
        for (String json : evidenceJsons) {
            CompilationResult result = this.compiler.compile(json);
            assertTrue(result.isSuccess(), "Expected evidence compile to succeed: " + result.getErrors());
        }

        List<String> ruleJsons = TestResourceLoader.loadJsonFiles("rule/schema/valid");
        for (String json : ruleJsons) {
            CompilationResult result = this.compiler.compile(json);
            assertTrue(result.isSuccess(), "Expected rule compile to succeed: " + result.getErrors());
        }

        CompilationResult rulesetResult = this.compiler.compile(rulesetJson);
        rulesetResult.getErrors().forEach(System.out::println);
        assertTrue(rulesetResult.isSuccess(), "Expected ruleset compile to succeed");
    }
}
