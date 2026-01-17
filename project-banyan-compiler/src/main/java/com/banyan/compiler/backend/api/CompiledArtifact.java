package com.banyan.compiler.backend.api;

import com.banyan.compiler.enums.ArtifactType;

public interface CompiledArtifact<T> {
    String id();
    ArtifactType type();
    int version();
    CompilationMetadata metadata();
    T payload();
}
