package com.banyan.compiler.backend.context;

import com.banyan.compiler.backend.api.CompilationErrorCode;
import com.banyan.compiler.backend.api.CompilationException;
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

class CompilationContextTest {

    @Test
    void registerAndResolve_roundTrip() {
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
        CompiledEvidenceType payload = new CompiledEvidenceType(
                "LOGIN_ATTEMPT",
                1,
                Map.of("count", new EvidenceField("count", EvidenceValueType.INTEGER, true))
        );
        CompiledEvidenceTypeArtifact artifact = new CompiledEvidenceTypeArtifact(
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

        ctx.register(artifact);

        assertEquals(artifact, ctx.resolve(ArtifactType.EvidenceType, "LOGIN_ATTEMPT", 1));
    }

    @Test
    void resolve_missingArtifact_throws() {
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());

        CompilationException ex = assertThrows(
                CompilationException.class,
                () -> ctx.resolve(ArtifactType.Rule, "missing", 1)
        );

        assertEquals(CompilationErrorCode.MISSING_DEPENDENCY, ex.getErrorCode());
    }

    @Test
    void register_afterFreeze_throws() {
        CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
        ctx.freeze();

        CompilationException ex = assertThrows(
                CompilationException.class,
                () -> ctx.register(new DummyArtifact())
        );

        assertEquals(CompilationErrorCode.CONTEXT_FREEZE, ex.getErrorCode());
    }

    private static final class DummyArtifact extends com.banyan.compiler.backend.api.AbstractCompiledArtifact<String> {
        private DummyArtifact() {
            super(
                    "dummy",
                    1,
                    ArtifactType.Task,
                    "payload",
                    new com.banyan.compiler.backend.api.CompilationMetadata(
                            "test",
                            System.currentTimeMillis(),
                            "hash"
                    ),
                    List.of()
            );
        }
    }
}