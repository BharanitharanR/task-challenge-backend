package com.banyan.compiler.backend.rule;


import com.banyan.compiler.backend.api.AbstractCompiledArtifact;
import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.enums.ArtifactType;

public class CompiledRuleArtifact extends AbstractCompiledArtifact<CompiledRule> {
    public CompiledRuleArtifact(String id, int version, CompiledRule field, CompilationMetadata metadata)
    {
        super(id,version,ArtifactType.Rule,field,metadata);
    }
}
