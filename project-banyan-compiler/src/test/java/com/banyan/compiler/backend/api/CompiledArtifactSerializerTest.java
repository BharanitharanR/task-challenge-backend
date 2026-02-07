package com.banyan.compiler.backend.api;

import com.banyan.compiler.backend.evidence.CompiledEvidenceType;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceField;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.EvidenceValueType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompiledArtifactSerializerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialize_compiledArtifact_containsEnvelopeFields() throws Exception {
        CompiledEvidenceType payload = new CompiledEvidenceType(
                "LOGIN_ATTEMPT",
                1,
                Map.of("count", new EvidenceField("count", EvidenceValueType.INTEGER, true))
        );
        CompiledEvidenceTypeArtifact artifact = new CompiledEvidenceTypeArtifact(
                "LOGIN_ATTEMPT",
                1,
                payload,
                new CompilationMetadata("test", System.currentTimeMillis(), "hash"),
                List.of(new ArtifactReference(ArtifactType.EvidenceType, "LOGIN_ATTEMPT", 1))
        );

        String json = mapper.writeValueAsString(artifact);

        assertTrue(json.contains("\"id\""));
        assertTrue(json.contains("\"version\""));
        assertTrue(json.contains("\"artifactType\""));
        assertTrue(json.contains("\"metadata\""));
        assertTrue(json.contains("\"dependencies\""));
        assertTrue(json.contains("\"payload\""));
    }
}