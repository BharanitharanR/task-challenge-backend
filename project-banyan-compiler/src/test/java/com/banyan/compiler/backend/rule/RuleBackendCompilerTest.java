package com.banyan.compiler.backend.rule;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceBackendCompiler;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.semantics.RuleSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleBackendCompilerTest {


    private static final RuleSemanticValidator validator = new RuleSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "rule/semantic/valid";

    @Test
    public void compile_validRule_shouldProduceCompiledArtifact() throws JsonProcessingException {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
        CompilationContext ctx = new CompilationContext();
        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
            RuleBackendCompiler compiler = new RuleBackendCompiler();

            CompiledRuleArtifact compileEvidence =  compiler.compile(mapper.readTree(json),ctx);
            ctx.register(compileEvidence);
            assertEquals(ArtifactType.Rule,compileEvidence.type());
        }
        System.out.println(ctx.resolve(ArtifactType.Rule,"business_hours_only",2).payload().toString());
        assertEquals("business_hours_only",ctx.resolve(ArtifactType.Rule,"business_hours_only",2).id());

    }
}
