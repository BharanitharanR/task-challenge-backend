package com.banyan.compiler.backend.rule;


import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.evidence.EvidenceField;
import com.banyan.compiler.enums.ArtifactType;

import java.util.Map;

public class CompiledRuleArtifact implements CompiledArtifact<CompiledRule> {


    private final String id;
    private final int version;
    private final CompiledRule field;
    private final CompilationMetadata metadata;

    public CompiledRuleArtifact(String id, int version, CompiledRule field, CompilationMetadata metadata) {
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
        return ArtifactType.Rule;
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
    public CompiledRule payload() {
        return this.field;
    }
}
