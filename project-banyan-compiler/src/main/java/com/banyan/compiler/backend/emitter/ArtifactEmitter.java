package com.banyan.compiler.backend.emitter;

import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.backend.outcome.CompilationRoot;

public interface ArtifactEmitter {
    boolean supports(CompilationRoot root);
    void emit(CompilationOutcome outcome); // throws ArtifactEmissionException;
}
