package com.banyan.compiler.backend.evidence;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.spi.BackendCompiler;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvidenceBackendCompilerTest {

    private static final EvidenceTypeSemanticValidator validator = new EvidenceTypeSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "evidence-type/semantic/valid";

    @Test
    public void compile_validEvidenceType_shouldProduceCompiledArtifact() throws JsonProcessingException {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
        CompilationContext ctx = new CompilationContext();
        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);
            EvidenceBackendCompiler compiler = new EvidenceBackendCompiler();

             CompiledEvidenceTypeArtifact compileEvidence =  compiler.compile(mapper.readTree(json),ctx);
             ctx.register(compileEvidence);
            assertEquals(ArtifactType.EvidenceType,compileEvidence.type());
        }
        System.out.println(ctx.resolve(ArtifactType.EvidenceType,"LOGIN_ATTEMPT",1).payload().toString());
        assertEquals("LOGIN_ATTEMPT",ctx.resolve(ArtifactType.EvidenceType,"LOGIN_ATTEMPT",1).id());

    }


}
