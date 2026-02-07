package com.banyan.compiler.backend.outcome;

import com.banyan.compiler.backend.api.CompilationErrorCode;
import com.banyan.compiler.backend.api.CompilationException;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceType;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceField;
import com.banyan.compiler.backend.spi.AbstractBackendCompiler;
import com.banyan.compiler.compatibility.bootstrap.CompilerCompatibilityBootstrap;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.EvidenceValueType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompilationOutcomeBuilderTest {

    @Test
    void build_withMissingRoot_returnsFailureOutcome() {
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
        CompilationOutcomeBuilder builder = new CompilationOutcomeBuilder(
                ctx,
                new CompilationRoot(ArtifactType.Challenge, "missing", 1)
        );

        CompilationOutcome outcome = builder.build();

        assertFalse(outcome.isSuccess());
        assertFalse(outcome.errors().isEmpty());
        assertTrue(outcome.getReachableArtifacts().isEmpty());
    }

    @Test
    void build_withReachableArtifacts_collectsDependencies() {
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());

        CompiledEvidenceType payload = new CompiledEvidenceType(
                "LOGIN_ATTEMPT",
                1,
                Map.of("count", new EvidenceField("count", EvidenceValueType.INTEGER, true))
        );
        CompiledEvidenceTypeArtifact rootArtifact = new CompiledEvidenceTypeArtifact(
                "LOGIN_ATTEMPT",
                1,
                payload,
                new com.banyan.compiler.backend.api.CompilationMetadata(
                        "test",
                        System.currentTimeMillis(),
                        "hash"
                ),
                List.of()
        );
        ctx.register(rootArtifact);

        CompilationOutcomeBuilder builder = new CompilationOutcomeBuilder(
                ctx,
                new CompilationRoot(ArtifactType.EvidenceType, "LOGIN_ATTEMPT", 1)
        );

        CompilationOutcome outcome = builder.build();

        assertTrue(outcome.isSuccess());
        assertEquals(1, outcome.getReachableArtifacts().size());
    }

    @Test
    void build_collectsMissingDependencyErrors() {
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());

        CompilationOutcomeBuilder builder = new CompilationOutcomeBuilder(
                ctx,
                new CompilationRoot(ArtifactType.Rule, "missing", 1)
        );

        CompilationOutcome outcome = builder.build();

        assertFalse(outcome.errors().isEmpty());
        CompilationException error = outcome.errors().getFirst();
        assertEquals(CompilationErrorCode.MISSING_DEPENDENCY, error.getErrorCode());
    }

}