package com.banyan.compiler.backend.challenge;

import com.banyan.compiler.backend.api.AbstractCompiledArtifact;
import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.task.CompiledTask;
import com.banyan.compiler.enums.ArtifactType;

public final class CompiledChallengeArtifact extends AbstractCompiledArtifact<CompiledChallenge> {

    public CompiledChallengeArtifact(String id, int version, CompiledChallenge field, CompilationMetadata metadata) {
        super(id,version, ArtifactType.Challenge,field,metadata);
    }
}
