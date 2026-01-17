package com.banyan.compiler.backend.ruleset;

import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.enums.ArtifactType;

public class CompiledRulesetArtifact implements CompiledArtifact<CompiledRuleset> {


    private final String id;
    private final int version;
    private final CompiledRuleset field;
    private final CompilationMetadata metadata;

    public CompiledRulesetArtifact(String id, int version, CompiledRuleset field, CompilationMetadata metadata) {
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
        return ArtifactType.Ruleset;
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
    public CompiledRuleset payload() {
        return this.field;
    }
}