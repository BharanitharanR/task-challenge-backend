package com.banyan.compiler.backend.evidence;

import com.banyan.compiler.backend.api.AbstractCompiledArtifact;
import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.enums.ArtifactType;


import java.util.Map;

public final class CompiledEvidenceTypeArtifact extends AbstractCompiledArtifact<Map<String,EvidenceField>> {

    public CompiledEvidenceTypeArtifact(String id,int version,Map<String,EvidenceField> fields,CompilationMetadata metadata) {
        super(id,version, ArtifactType.EvidenceType,fields,metadata);
    }

}
