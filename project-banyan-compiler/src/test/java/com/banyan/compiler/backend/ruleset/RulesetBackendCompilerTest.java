package com.banyan.compiler.backend.ruleset;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceBackendCompiler;
import com.banyan.compiler.backend.rule.CompiledRuleArtifact;
import com.banyan.compiler.backend.rule.RuleBackendCompiler;
import com.banyan.compiler.compatibility.bootstrap.CompilerCompatibilityBootstrap;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.semantics.RuleSemanticValidator;
import com.banyan.compiler.semantics.RuleSetSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesetBackendCompilerTest {

    private static final RuleSetSemanticValidator validator = new RuleSetSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "ruleset/semantic/valid";
    private static final String RULE_RESOURCE = "rule/semantic/valid";
    private static final String EVIDENCE_RESOURCE = "evidence-type/semantic/valid";

    @Test
    public void compile_validRuleset_shouldProduceCompiledArtifact() throws JsonProcessingException {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
        registerEvidenceTypes(ctx);
        registerRules(ctx);
        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
            if(json.contains("nested_multi_layer_ruleset")) {
                RuleSetBackendCompiler compiler = new RuleSetBackendCompiler();

                CompiledRulesetArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
                ctx.register(compileEvidence);
                assertEquals(ArtifactType.Ruleset, compileEvidence.type());
            }
        }
        //System.out.println(ctx.resolve(ArtifactType.Ruleset,"business_hours_only",2).payload().toString());
        //assertEquals("business_hours_only",ctx.resolve(ArtifactType.Rule,"business_hours_only",2).id());

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
}
