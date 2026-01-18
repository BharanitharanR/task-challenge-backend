package com.banyan.compiler.backend.api;

import com.banyan.compiler.enums.ArtifactType;

public abstract class AbstractCompiledArtifact<T>
        implements CompiledArtifact<T> {

    private final String id;
    private final int version;
    private final ArtifactType type;
    private final T payload;
    private final CompilationMetadata metadata;

    protected AbstractCompiledArtifact(
            String id,
            int version,
            ArtifactType type,
            T payload,
            CompilationMetadata metadata
    ) {
        this.id = id;
        this.version = version;
        this.type = type;
        this.payload = payload;
        this.metadata = metadata;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public ArtifactType type() {
        return type;
    }

    @Override
    public CompilationMetadata metadata() {
        return metadata;
    }

    @Override
    public T payload() {
        return payload;
    }
}
