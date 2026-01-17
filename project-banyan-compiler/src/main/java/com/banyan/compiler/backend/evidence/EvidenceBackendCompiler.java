package com.banyan.compiler.backend.evidence;
import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.spi.AbstractBackendCompiler;
import com.banyan.compiler.enums.EvidenceValueType;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class EvidenceBackendCompiler extends AbstractBackendCompiler<CompiledEvidenceTypeArtifact> {
    @Override
    public CompiledEvidenceTypeArtifact compile(JsonNode validatedDsl, CompilationContext context) {
        String id = readId(validatedDsl);
        int version = readVersion(validatedDsl);
        Map<String, EvidenceField> fields = new LinkedHashMap<>();
        CompilationMetadata metadata = metadata(validatedDsl);

        for (JsonNode f : validatedDsl.at("/spec/fields")) {
            fields.put(
                    f.get("name").asText(),
                    new EvidenceField(
                            f.get("name").asText(),
                            EvidenceValueType.valueOf(f.get("type").asText()),
                            f.get("required").asBoolean()
                    )
            );
        }
        return new CompiledEvidenceTypeArtifact(id,version,fields,metadata);
    }



}
