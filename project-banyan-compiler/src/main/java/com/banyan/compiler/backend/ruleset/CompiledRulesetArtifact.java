package com.banyan.compiler.backend.ruleset;

import com.banyan.compiler.backend.api.AbstractCompiledArtifact;
import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.enums.ArtifactType;

public class CompiledRulesetArtifact extends AbstractCompiledArtifact<CompiledRuleset> {
    public CompiledRulesetArtifact(String id, int version, CompiledRuleset field, CompilationMetadata metadata) {
        super(id,version, ArtifactType.Ruleset,field,metadata);
    }

}