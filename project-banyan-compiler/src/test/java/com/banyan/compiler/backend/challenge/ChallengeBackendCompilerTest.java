package com.banyan.compiler.backend.challenge;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceBackendCompiler;
import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.rule.CompiledRuleArtifact;
import com.banyan.compiler.backend.rule.RuleBackendCompiler;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.ruleset.RuleSetBackendCompiler;
import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.compiler.backend.task.TaskBackendCompiler;
import com.banyan.compiler.compatibility.bootstrap.CompilerBootstrapContext;
import com.banyan.compiler.compatibility.bootstrap.CompilerBootstrapContextImpl;
import com.banyan.compiler.compatibility.bootstrap.CompilerCompatibilityBootstrap;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.ChallengeSemanticValidator;
import com.banyan.compiler.semantics.TaskSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeBackendCompilerTest {
    private static final ChallengeSemanticValidator validator = new ChallengeSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "challenge/semantic/valid";
    private static final String TASK_VALID_RESOURCE = "task/semantic/valid";
    private static final String RULESET_VALID_RESOURCE = "ruleset/semantic/valid";
    private static final String RULE_VALID_RESOURCE = "rule/semantic/valid";
    private static final String EVIDENCE_VALID_RESOURCE = "evidence-type/semantic/valid";
    static List<String> jsons = new ArrayList<>();
    static List<String> taskJsons = new ArrayList<>();
    static List<String> ruleSetJsons = new ArrayList<>();
    static List<String> ruleJsons = new ArrayList<>();
    static List<String> evidenceTypeJsons = new ArrayList<>();

    static CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
@BeforeAll
static void register() throws JsonProcessingException {

     jsons= TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
    taskJsons = TestResourceLoader.loadJsonFiles(TASK_VALID_RESOURCE);
    ruleSetJsons = TestResourceLoader.loadJsonFiles(RULESET_VALID_RESOURCE);
    ruleJsons = TestResourceLoader.loadJsonFiles(RULE_VALID_RESOURCE);
    evidenceTypeJsons = TestResourceLoader.loadJsonFiles(EVIDENCE_VALID_RESOURCE);
    // Evidence-type Registration
    for (String json : evidenceTypeJsons) {
        EvidenceBackendCompiler compiler = new EvidenceBackendCompiler();
        System.out.println("Evidence type: "+json);
        CompiledEvidenceTypeArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
        ctx.register(compileEvidence);
    }
    // Rules Registration
    for (String json : ruleJsons) {
        RuleBackendCompiler compiler = new RuleBackendCompiler();
        System.out.println("Rule: "+json);
        CompiledRuleArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
        ctx.register(compileEvidence);
        ctx.zipContext();
    }
    // Rules Registration
    for (String json : ruleJsons) {
        RuleBackendCompiler compiler = new RuleBackendCompiler();
        System.out.println("Rule: "+json);
        CompiledRuleArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
        ctx.register(compileEvidence);
        ctx.zipContext();
    }
    // Ruleset Registration
    for (String json : ruleSetJsons) {
        RuleSetBackendCompiler compiler = new RuleSetBackendCompiler();
        System.out.println("Ruleset: "+json);
        CompiledRulesetArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
        ctx.register(compileEvidence);
        ctx.zipContext();
    }
    //Task Registration
    for (String json : taskJsons) {
        TaskBackendCompiler compiler = new TaskBackendCompiler();
        System.out.println("Task: "+json);
        CompiledTaskArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
        ctx.zipContext();
        ctx.register(compileEvidence);
        ctx.zipContext();
    }

}
    @Test
    public void compile_validChallenge_shouldProduceCompiledArtifact() throws JsonProcessingException {

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);

                ChallengeBackendCompiler compiler = new ChallengeBackendCompiler();

                CompiledChallengeArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
                ctx.register(compileEvidence);
                assertEquals(ArtifactType.Challenge, compileEvidence.type());

        }
        System.out.println(ctx.resolve(ArtifactType.Challenge,"unique_task_challenge",1).payload().toString());
        assertEquals("unique_task_challenge",ctx.resolve(ArtifactType.Challenge,"unique_task_challenge",1).id());
        ctx.zipContext();
    }
}
