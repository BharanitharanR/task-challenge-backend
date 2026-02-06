package com.banyan.compiler.backend.outcome;

import com.banyan.compiler.backend.api.CompilationException;
import com.banyan.compiler.backend.api.CompiledArtifact;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public final class  CompilationOutcome {

    private final CompilationRoot root;
    private final Set<CompiledArtifact<?>> reachableArtifacts;
    private final List<CompilationException> errors;

    public  <E> CompilationOutcome(CompilationRoot root, Set<CompiledArtifact<?>> of, List<CompilationException> errors) {
        this.root = root;
        this.reachableArtifacts = of;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public Set<CompiledArtifact<?>> getReachableArtifacts() {
        return reachableArtifacts;
    }

    public List<CompilationException> errors() {
        return errors;
    }
}
