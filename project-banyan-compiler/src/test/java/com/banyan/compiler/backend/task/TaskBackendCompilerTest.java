package com.banyan.compiler.backend.task;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceBackendCompiler;
import com.banyan.compiler.backend.rule.CompiledRuleArtifact;
import com.banyan.compiler.backend.rule.RuleBackendCompiler;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.ruleset.RuleSetBackendCompiler;
import com.banyan.compiler.compatibility.bootstrap.CompilerCompatibilityBootstrap;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.semantics.RuleSemanticValidator;
import com.banyan.compiler.semantics.RuleSetSemanticValidator;
import com.banyan.compiler.semantics.TaskSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskBackendCompilerTest {


    private static final TaskSemanticValidator validator = new TaskSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "task/semantic/valid";
    private static final String RULESET_RESOURCE = "ruleset/semantic/valid";
    private static final String RULE_RESOURCE = "rule/semantic/valid";
    private static final String EVIDENCE_RESOURCE = "evidence-type/semantic/valid";

    @Test
    public void compile_validtask_shouldProduceCompiledArtifact() throws JsonProcessingException {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
        registerEvidenceTypes(ctx);
        registerRules(ctx);
        registerRulesets(ctx);
        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);

                TaskBackendCompiler compiler = new TaskBackendCompiler();

                CompiledTaskArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
                ctx.register(compileEvidence);
                assertEquals(ArtifactType.Task, compileEvidence.type());

        }
        System.out.println(ctx.resolve(ArtifactType.Task,"task_with_actions",1).payload().toString());
        assertEquals("task_with_actions",ctx.resolve(ArtifactType.Task,"task_with_actions",1).id());

    }

    private void registerEvidenceTypes(CompilationContext ctx) throws JsonProcessingException {
        EvidenceTypeSemanticValidator evidenceValidator = new EvidenceTypeSemanticValidator();
        List<String> jsons = TestResourceLoader.loadJsonFiles(EVIDENCE_RESOURCE);
        for (String json : jsons) {
            List<String> errors = evidenceValidator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no evidence errors but got: " + errors);
            EvidenceBackendCompiler compiler = new EvidenceBackendCompiler();
            CompiledEvidenceTypeArtifact artifact = compiler.compile(mapper.readTree(json), ctx);
            ctx.register(artifact);
        }
    }

    private void registerRules(CompilationContext ctx) throws JsonProcessingException {
        RuleSemanticValidator ruleValidator = new RuleSemanticValidator();
        List<String> jsons = TestResourceLoader.loadJsonFiles(RULE_RESOURCE);
        for (String json : jsons) {
            List<String> errors = ruleValidator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no rule errors but got: " + errors);
            RuleBackendCompiler compiler = new RuleBackendCompiler();
            CompiledRuleArtifact artifact = compiler.compile(mapper.readTree(json), ctx);
            ctx.register(artifact);
        }
    }

    private void registerRulesets(CompilationContext ctx) throws JsonProcessingException {
        RuleSetSemanticValidator rulesetValidator = new RuleSetSemanticValidator();
        List<String> jsons = TestResourceLoader.loadJsonFiles(RULESET_RESOURCE);
        for (String json : jsons) {
            List<String> errors = rulesetValidator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no ruleset errors but got: " + errors);
            RuleSetBackendCompiler compiler = new RuleSetBackendCompiler();
            CompiledRulesetArtifact artifact = compiler.compile(mapper.readTree(json), ctx);
            ctx.register(artifact);
        }
    }
}
