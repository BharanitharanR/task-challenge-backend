package com.banyan.compiler.backend.outcome;

import com.banyan.compiler.enums.ArtifactType;

public record CompilationRoot(
        ArtifactType type,
        String id,
        int version
) {}

