package com.banyan.orchestrator;

import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.CompilationState;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Compilation report containing timing, counts, and diagnostic information.
 * Immutable snapshot of compilation execution.
 */
public final class CompilationReport {
    
    private final Instant startTime;
    private final Instant endTime;
    private final Duration totalDuration;
    
    // Phase timings
    private final Duration sourceParsingDuration;
    private final Duration frontendCompilationDuration;
    private final Duration backendCompilationDuration;
    private final Duration outcomeBuildingDuration;
    private final Duration emissionDuration;
    
    // Artifact counts
    private final Map<ArtifactType, Integer> sourceCounts;
    private final Map<ArtifactType, Integer> compiledArtifactCounts;
    private final int totalReachableArtifacts;
    
    // Error and warning counts
    private final int totalErrors;
    private final int totalWarnings;
    
    // Compilation state
    private final CompilationState state;
    
    private CompilationReport(Builder builder) {
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.totalDuration = builder.totalDuration;
        this.sourceParsingDuration = builder.sourceParsingDuration;
        this.frontendCompilationDuration = builder.frontendCompilationDuration;
        this.backendCompilationDuration = builder.backendCompilationDuration;
        this.outcomeBuildingDuration = builder.outcomeBuildingDuration;
        this.emissionDuration = builder.emissionDuration;
        this.sourceCounts = Map.copyOf(builder.sourceCounts);
        this.compiledArtifactCounts = Map.copyOf(builder.compiledArtifactCounts);
        this.totalReachableArtifacts = builder.totalReachableArtifacts;
        this.totalErrors = builder.totalErrors;
        this.totalWarnings = builder.totalWarnings;
        this.state = builder.state;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public Instant getEndTime() {
        return endTime;
    }
    
    public Duration getTotalDuration() {
        return totalDuration;
    }
    
    public Duration getSourceParsingDuration() {
        return sourceParsingDuration;
    }
    
    public Duration getFrontendCompilationDuration() {
        return frontendCompilationDuration;
    }
    
    public Duration getBackendCompilationDuration() {
        return backendCompilationDuration;
    }
    
    public Duration getOutcomeBuildingDuration() {
        return outcomeBuildingDuration;
    }
    
    public Duration getEmissionDuration() {
        return emissionDuration;
    }
    
    public Map<ArtifactType, Integer> getSourceCounts() {
        return sourceCounts;
    }
    
    public Map<ArtifactType, Integer> getCompiledArtifactCounts() {
        return compiledArtifactCounts;
    }
    
    public int getTotalReachableArtifacts() {
        return totalReachableArtifacts;
    }
    
    public int getTotalErrors() {
        return totalErrors;
    }
    
    public int getTotalWarnings() {
        return totalWarnings;
    }
    
    public CompilationState getState() {
        return state;
    }
    
    @Override
    public String toString() {
        return String.format(
            "CompilationReport{" +
            "totalDuration=%s, " +
            "sourceParsing=%s, " +
            "frontend=%s, " +
            "backend=%s, " +
            "outcome=%s, " +
            "emission=%s, " +
            "sources=%s, " +
            "artifacts=%s, " +
            "errors=%d, " +
            "warnings=%d, " +
            "state=%s" +
            "}",
            totalDuration,
            sourceParsingDuration,
            frontendCompilationDuration,
            backendCompilationDuration,
            outcomeBuildingDuration,
            emissionDuration,
            sourceCounts,
            compiledArtifactCounts,
            totalErrors,
            totalWarnings,
            state
        );
    }
    
    public static class Builder {
        private Instant startTime;
        private Instant endTime;
        private Duration totalDuration;
        private Duration sourceParsingDuration;
        private Duration frontendCompilationDuration;
        private Duration backendCompilationDuration;
        private Duration outcomeBuildingDuration;
        private Duration emissionDuration;
        private final Map<ArtifactType, Integer> sourceCounts = new java.util.HashMap<>();
        private final Map<ArtifactType, Integer> compiledArtifactCounts = new java.util.HashMap<>();
        private int totalReachableArtifacts;
        private int totalErrors;
        private int totalWarnings;
        private CompilationState state;
        
        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            this.totalDuration = Duration.between(startTime, endTime);
            return this;
        }
        
        public Builder sourceParsingDuration(Duration duration) {
            this.sourceParsingDuration = duration;
            return this;
        }
        
        public Builder frontendCompilationDuration(Duration duration) {
            this.frontendCompilationDuration = duration;
            return this;
        }
        
        public Builder backendCompilationDuration(Duration duration) {
            this.backendCompilationDuration = duration;
            return this;
        }
        
        public Builder outcomeBuildingDuration(Duration duration) {
            this.outcomeBuildingDuration = duration;
            return this;
        }
        
        public Builder emissionDuration(Duration duration) {
            this.emissionDuration = duration;
            return this;
        }
        
        public Builder sourceCounts(Map<ArtifactType, Integer> counts) {
            this.sourceCounts.putAll(counts);
            return this;
        }
        
        public Builder compiledArtifactCounts(Map<ArtifactType, Integer> counts) {
            this.compiledArtifactCounts.putAll(counts);
            return this;
        }
        
        public Builder totalReachableArtifacts(int count) {
            this.totalReachableArtifacts = count;
            return this;
        }
        
        public Builder totalErrors(int errors) {
            this.totalErrors = errors;
            return this;
        }
        
        public Builder totalWarnings(int warnings) {
            this.totalWarnings = warnings;
            return this;
        }
        
        public Builder state(CompilationState state) {
            this.state = state;
            return this;
        }
        
        public CompilationReport build() {
            return new CompilationReport(this);
        }
    }
}