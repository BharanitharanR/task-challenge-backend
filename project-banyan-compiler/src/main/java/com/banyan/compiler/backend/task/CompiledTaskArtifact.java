package com.banyan.compiler.backend.task;

import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.ruleset.CompiledRuleset;
import com.banyan.compiler.enums.ArtifactType;

public class CompiledTaskArtifact implements CompiledArtifact<CompiledTask> {

    private final String id;
    private final int version;
    private final CompiledTask field;
    private final CompilationMetadata metadata;

    public CompiledTaskArtifact(String id, int version, CompiledTask field, CompilationMetadata metadata) {
        this.id = id;
        this.version = version;
        this.field = field;
        this.metadata = metadata;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public ArtifactType type() {
        return ArtifactType.Task;
    }

    @Override
    public int version() {
        return this.version;
    }

    @Override
    public CompilationMetadata metadata() {
        return this.metadata;
    }

    @Override
    public CompiledTask payload() {
        return this.field;
    }
}
