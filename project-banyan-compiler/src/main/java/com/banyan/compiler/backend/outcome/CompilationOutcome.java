package com.banyan.compiler.backend.outcome;

import com.banyan.compiler.backend.api.CompilationException;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.enums.ArtifactType;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public final class CompilationOutcome {

    private final CompilationRoot root;
    private final Set<CompiledArtifact<?>> reachableArtifacts;
    private final List<CompilationException> errors;

    public boolean isSuccess() {
        return errors.isEmpty();
    }
}
