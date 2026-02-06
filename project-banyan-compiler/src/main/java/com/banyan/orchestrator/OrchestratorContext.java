package com.banyan.orchestrator;

import com.banyan.compiler.enums.ArtifactType;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for controlled access to the source library.
 * The orchestrator exposes these methods instead of passing the library directly.
 */
public interface OrchestratorContext {

    /**
     * Gets all source units of a specific artifact type.
     * 
     * @param type The artifact type
     * @return Collection of source units, empty if none found
     */
    Collection<SourceUnit> sources(ArtifactType type);

    /**
     * Gets a specific source unit by type, id, and version.
     * 
     * @param type The artifact type
     * @param id The artifact id
     * @param version The artifact version
     * @return Optional containing the source unit if found
     */
    Optional<SourceUnit> source(ArtifactType type, String id, int version);

    /**
     * Checks if the library contains any sources of the specified type.
     * 
     * @param type The artifact type
     * @return true if sources exist for this type
     */
    boolean hasSources(ArtifactType type);
}