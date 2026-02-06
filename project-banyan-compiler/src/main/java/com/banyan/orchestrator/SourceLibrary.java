package com.banyan.orchestrator;

import com.banyan.compiler.enums.ArtifactType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory source library representing a read-only indexed view of input sources.
 * Built once per compilation request and lives only for the duration of orchestration.
 * 
 * This is the "library" in the library analogy - immutable after construction,
 * indexed by ArtifactType, id, and version.
 */
public final class  SourceLibrary {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    // Primary index: type -> id -> version -> SourceUnit
    private final Map<ArtifactType, Map<String, Map<Integer, SourceUnit>>> library;
    
    // Reverse lookup: SymbolKey -> SourceUnit
    private final Map<SymbolKey, SourceUnit> symbolIndex;
    
    private SourceLibrary(Map<ArtifactType, Map<String, Map<Integer, SourceUnit>>> library,
                         Map<SymbolKey, SourceUnit> symbolIndex) {
        this.library = Collections.unmodifiableMap(library);
        this.symbolIndex = Collections.unmodifiableMap(symbolIndex);
    }
    
    /**
     * Creates a SourceLibrary from a collection of JSON strings.
     * 
     * @param jsonSources Collection of JSON source strings
     * @return A new SourceLibrary instance
     * @throws IllegalArgumentException if any source is invalid
     */
    public static SourceLibrary fromJsonSources(Collection<String> jsonSources) {
        Map<ArtifactType, Map<String, Map<Integer, SourceUnit>>> library = new HashMap<>();
        Map<SymbolKey, SourceUnit> symbolIndex = new HashMap<>();
        
        for (String json : jsonSources) {
            try {
                JsonNode content = MAPPER.readTree(json);
                SourceUnit unit = new SourceUnit(content);
                
                // Add to primary index
                library.computeIfAbsent(unit.type(), k -> new HashMap<>())
                       .computeIfAbsent(unit.id(), k -> new HashMap<>())
                       .put(unit.version(), unit);
                
                // Add to symbol index
                SymbolKey key = new SymbolKey(unit.type(), unit.id(), unit.version());
                symbolIndex.put(key, unit);
                
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid source JSON: " + e.getMessage(), e);
            }
        }
        
        return new SourceLibrary(library, symbolIndex);
    }
    
    /**
     * Gets all source units of a specific artifact type.
     * 
     * @param type The artifact type
     * @return Collection of source units, empty if none found
     */
    public Collection<SourceUnit> sources(ArtifactType type) {
        Map<String, Map<Integer, SourceUnit>> typeMap = library.get(type);
        if (typeMap == null) {
            return Collections.emptyList();
        }
        
        return typeMap.values().stream()
                     .flatMap(versionMap -> versionMap.values().stream())
                     .collect(Collectors.toList());
    }
    
    /**
     * Gets a specific source unit by type, id, and version.
     * 
     * @param type The artifact type
     * @param id The artifact id
     * @param version The artifact version
     * @return Optional containing the source unit if found
     */
    public Optional<SourceUnit> source(ArtifactType type, String id, int version) {
        SymbolKey key = new SymbolKey(type, id, version);
        return Optional.ofNullable(symbolIndex.get(key));
    }
    
    /**
     * Checks if the library contains any sources of the specified type.
     * 
     * @param type The artifact type
     * @return true if sources exist for this type
     */
    public boolean hasSources(ArtifactType type) {
        return library.containsKey(type) && !library.get(type).isEmpty();
    }
    
    /**
     * Gets the total number of source units in the library.
     * 
     * @return Total count of all source units
     */
    public int size() {
        return symbolIndex.size();
    }
    
    /**
     * Gets all artifact types present in the library.
     * 
     * @return Set of artifact types
     */
    public Set<ArtifactType> getArtifactTypes() {
        return new HashSet<>(library.keySet());
    }
    
    /**
     * Gets all source units in the library.
     * 
     * @return Collection of all source units
     */
    public Collection<SourceUnit> allSources() {
        return new ArrayList<>(symbolIndex.values());
    }
    
    /**
     * Symbol key for efficient lookup.
     */
    private static final class SymbolKey {
        private final ArtifactType type;
        private final String id;
        private final int version;
        
        public SymbolKey(ArtifactType type, String id, int version) {
            this.type = type;
            this.id = id;
            this.version = version;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SymbolKey symbolKey = (SymbolKey) o;
            return version == symbolKey.version &&
                   type == symbolKey.type &&
                   Objects.equals(id, symbolKey.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(type, id, version);
        }
    }
}