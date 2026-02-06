package com.banyan.orchestrator;

import com.banyan.compiler.enums.ArtifactType;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a single source unit in the compilation process.
 * Immutable value object containing parsed JSON and metadata.
 */
public record SourceUnit(
        ArtifactType type,
        String id,
        int version,
        JsonNode content
) {
    
    /**
     * Creates a SourceUnit from JSON content.
     * 
     * @param content The parsed JSON content
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public SourceUnit(JsonNode content) {
        this(
            extractType(content),
            extractId(content),
            extractVersion(content),
            content
        );
    }
    
    private static ArtifactType extractType(JsonNode content) {
        String kind = content.path("kind").asText(null);
        if (kind == null || kind.isBlank()) {
            throw new IllegalArgumentException("SourceUnit must have a 'kind' field");
        }
        try {
            return ArtifactType.valueOf(kind);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown artifact type: " + kind, e);
        }
    }
    
    private static String extractId(JsonNode content) {
        String id = content.path("id").asText(null);
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("SourceUnit must have an 'id' field");
        }
        return id;
    }
    
    private static int extractVersion(JsonNode content) {
        return content.path("version").asInt(1);
    }
}