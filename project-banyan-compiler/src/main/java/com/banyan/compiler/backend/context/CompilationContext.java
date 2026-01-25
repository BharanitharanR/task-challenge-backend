package com.banyan.compiler.backend.context;

import com.banyan.compiler.backend.api.CompilationErrorCode;
import com.banyan.compiler.backend.api.CompilationException;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.compatibility.bootstrap.CompatibilityResolver;
import com.banyan.compiler.compatibility.bootstrap.CompilerBootstrapContext;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.CompilationState;

import com.banyan.compiler.enums.SymbolKey;

import io.smallrye.common.constraint.NotNull;

import java.util.HashMap;
import java.util.Map;


public class CompilationContext {
    @NotNull
    private final Map<SymbolKey, CompiledArtifact> symbolTable= new HashMap<>();
    private final CompilerBootstrapContext compatibility;
    private CompilationState state;
    public CompilationContext(CompilerBootstrapContext compatibility) {
        this.compatibility = compatibility;
        this.state = CompilationState.RUNNING;
    }

    public <A, B> CompatibilityResolver<A, B> compatibility(
            Class<A> left,
            Class<B> right
    ) {
        return compatibility.compatibility(left, right);
    }

    public void register(CompiledArtifact artifact){
        if (assertFreeze())
            throw new CompilationException(
                    CompilationErrorCode.CONTEXT_FREEZE,
                    artifact.type().toString()
            );
        symbolTable.put(new SymbolKey(artifact.type(),artifact.id(),artifact.version()),artifact);
    }

    public CompiledArtifact resolve(ArtifactType type, String id, int version) throws CompilationException
    {
        CompiledArtifact artifact =
                symbolTable.get(new SymbolKey(type, id, version));

        if (artifact == null) {
            throw new CompilationException(
                    CompilationErrorCode.MISSING_DEPENDENCY,
                    type + ":" + id + ":" + version
            );
        }
        return artifact;
    }

    private boolean assertFreeze() {
        return !( this.state== CompilationState.RUNNING);
    }

    public void freeze(){ this.state = CompilationState.COMPLETED;}
}
