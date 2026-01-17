package com.banyan.compiler.backend.spi;

import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.context.CompilationContext;
import com.fasterxml.jackson.databind.JsonNode;

public interface BackendCompiler<T extends CompiledArtifact<?>> {

    /**
     * Compile a validated DSL JsonNode into a runtime-ready artifact.
     */
    T compile(JsonNode validatedDsl, CompilationContext context);
}
