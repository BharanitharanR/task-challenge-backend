package com.banyan.compiler.core;

import com.banyan.compiler.pipeline.EvidenceTypeCompilationPipeline;
import com.banyan.compiler.pipeline.RuleCompilationPipeline;
import com.banyan.compiler.pipeline.RuleSetCompilationPipeline;
import com.banyan.compiler.pipeline.TaskCompilationPipeline;
import com.banyan.compiler.registry.CompilationPipelineRegistry;
import com.banyan.compiler.testutil.TestResourceLoader;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BanyanCompilerTest {
    public CompilationPipelineRegistry registry = new CompilationPipelineRegistry();
    public BanyanCompiler compiler;
    public BanyanCompilerTest(){
        this.registry.register("Rule",new RuleCompilationPipeline());
        this.registry.register("Ruleset",new RuleSetCompilationPipeline());
        this.registry.register("EvidenceType",new EvidenceTypeCompilationPipeline());
        this.registry.register("Task",new TaskCompilationPipeline());
        this.compiler = new BanyanCompiler(this.registry);
    }

    private static final String EVIDENCE_VALID_RESOURCE = "evidence-type/schema/valid";
    private static final String EVIDENCE_INVALID_RESOURCE = "evidence-type/schema/invalid";
    private static final String RULE_VALID_RESOURCE = "rule/schema/valid";
    private static final String RULE_INVALID_RESOURCE = "rule/schema/invalid";

    @Test
    void allValidRulesShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(RULE_VALID_RESOURCE);
        for (String json : jsons) {
            CompilationResult result = this.compiler.compile(json);
            System.out.println(json);
            result.getErrors().forEach(System.out::println);
            assertTrue(result.isSuccess());
        }
    }

    @Test
    void allInvalidRulesShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(RULE_INVALID_RESOURCE);

        for (String json : jsons) {

            CompilationResult result = this.compiler.compile(json);
            assertTrue(!result.isSuccess());
        }
    }

    @Test
    void allValidTypesShouldPass() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(EVIDENCE_VALID_RESOURCE);

        for (String json : jsons) {

            CompilationResult result = this.compiler.compile(json);
            assertTrue(result.isSuccess());
        }
    }

    @Test
    void allInvalidEvidenceTypesShouldFail() {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(EVIDENCE_INVALID_RESOURCE);

        for (String json : jsons) {

            CompilationResult result = this.compiler.compile(json);
            assertTrue(!result.isSuccess());
        }
    }


}
