package com.banyan.orchestrator;

import com.banyan.compiler.backend.api.CompilationException;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.challenge.ChallengeBackendCompiler;
import com.banyan.compiler.backend.challenge.CompiledChallengeArtifact;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.emitter.ArtifactEmitter;
import com.banyan.compiler.backend.emitter.ZipEmitter;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceBackendCompiler;
import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.backend.outcome.CompilationOutcomeBuilder;
import com.banyan.compiler.backend.outcome.CompilationRoot;
import com.banyan.compiler.backend.rule.CompiledRuleArtifact;
import com.banyan.compiler.backend.rule.RuleBackendCompiler;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.ruleset.RuleSetBackendCompiler;
import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.compiler.backend.task.TaskBackendCompiler;
import com.banyan.compiler.compatibility.bootstrap.CompilerCompatibilityBootstrap;
import com.banyan.compiler.core.BanyanCompiler;
import com.banyan.compiler.core.CompilationResult;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.CompilationState;
import com.banyan.compiler.pipeline.*;
import com.banyan.compiler.registry.CompilationPipelineRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main orchestrator for the Banyan compilation process.
 * 
 * Entry point for compilation that owns the source lifecycle, compilation phases,
 * and produces CompilationOutcome and DAR files.
 * 
 * Follows the canonical flow:
 * ZIP INPUT → Source Parsing → In-memory Source Library (frozen) → Frontend Compilation
 * → Backend Compilation → CompilationContext (frozen) → CompilationOutcome → Emitter
 * → DAR + Compilation Report
 */
public final class Orchestrator implements OrchestratorContext {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Orchestrator.class);
    private final CompilationPipelineRegistry registry;
    private final BanyanCompiler compiler;
    private final Map<ArtifactType, BiFunction<JsonNode,CompilationContext, CompiledArtifact>> backendCompilers;
    private final static CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
    public Orchestrator() {
        this.registry = new CompilationPipelineRegistry();
        this.compiler = new BanyanCompiler(registry);
        this.backendCompilers = new HashMap<>();
        
        // Register frontend pipelines
        registerFrontendPipelines();
        
        // Register backend compilers
        registerBackendCompilers();
    }
    
    /**
     * Orchestrates the complete compilation process from ZIP input to DAR output.
     * 
     * @param zipFilePath Path to the input ZIP file containing source artifacts
     * @param outputDir Directory where the DAR file will be generated
     * @param rootType Root artifact type for compilation
     * @param rootId Root artifact ID
     * @param rootVersion Root artifact version
     * @return CompilationResult containing outcome and report
     */
    public CompilationResult orchestrate(String zipFilePath, String outputDir, 
                                       ArtifactType rootType, String rootId, int rootVersion) {
        Instant startTime = Instant.now();
        CompilationReport.Builder reportBuilder = CompilationReport.builder()
                .startTime(startTime)
                .state(CompilationState.RUNNING);
        
        try {
            // Phase 1: Source Parsing
            Instant parseStart = Instant.now();
            List<String> jsonSources = parseZipFile(zipFilePath);
            SourceLibrary sourceLibrary = SourceLibrary.fromJsonSources(jsonSources);
            Instant parseEnd = Instant.now();
            reportBuilder.sourceParsingDuration(Duration.between(parseStart, parseEnd))
                        .sourceCounts(countSourcesByType(sourceLibrary));
            
            // Phase 2: Frontend Compilation
            Instant frontendStart = Instant.now();
            List<CompilationResult> frontendResults = compileFrontend(sourceLibrary);
            Instant frontendEnd = Instant.now();
            reportBuilder.frontendCompilationDuration(Duration.between(frontendStart, frontendEnd));
            
            // Check for frontend errors
            List<String> frontendErrors = frontendResults.stream()
                    .filter(result -> !result.isSuccess())
                    .flatMap(result -> result.getErrors().stream())
                    .collect(Collectors.toList());
            
            if (!frontendErrors.isEmpty()) {
                reportBuilder.state(CompilationState.FAILED)
                            .totalErrors(frontendErrors.size())
                            .endTime(Instant.now());
                return CompilationResult.failure(frontendErrors);
            }
            
            // Phase 3: Backend Compilation
            Instant backendStart = Instant.now();
            CompilationContext context = compileBackend(frontendResults, reportBuilder);
            Instant backendEnd = Instant.now();
            reportBuilder.backendCompilationDuration(Duration.between(backendStart, backendEnd));
            
            // Phase 4: Build CompilationOutcome
            Instant outcomeStart = Instant.now();
            CompilationRoot root = new CompilationRoot(rootType, rootId, rootVersion);
            CompilationOutcomeBuilder outcomeBuilder = new CompilationOutcomeBuilder(context, root);
            CompilationOutcome outcome = outcomeBuilder.build();
            Instant outcomeEnd = Instant.now();
            reportBuilder.outcomeBuildingDuration(Duration.between(outcomeStart, outcomeEnd))
                        .totalReachableArtifacts(outcome.getReachableArtifacts().size())
                        .totalErrors(outcome.errors().size())
                        .totalWarnings(outcome.errors().size()); // TODO: Add warnings tracking
            
            if (!outcome.isSuccess()) {
                List<String> errors = outcome.errors().stream()
                        .map(e -> e.getMessage())
                        .collect(Collectors.toList());
                reportBuilder.state(CompilationState.FAILED)
                            .endTime(Instant.now());
                return CompilationResult.failure(errors);
            }
            
            // Phase 5: Emission
            Instant emissionStart = Instant.now();
            ArtifactEmitter emitter = new ZipEmitter();
            emitter.emit(outcome);
            Instant emissionEnd = Instant.now();
            reportBuilder.emissionDuration(Duration.between(emissionStart, emissionEnd))
                        .state(CompilationState.COMPLETED)
                        .endTime(Instant.now());
            
            CompilationReport report = reportBuilder.build();
            
            return CompilationResult.success(Map.of("outcome", outcome, "report", report), List.of());
            
        } catch (Exception e) {
            reportBuilder.state(CompilationState.FAILED)
                        .totalErrors(1)
                        .endTime(Instant.now());
            return CompilationResult.failure(List.of("Orchestration failed: " + e.getMessage()));
        }
    }
    
    // OrchestratorContext implementation - these methods are not used in the current design
    // The orchestrator manages the source library internally and doesn't expose direct access
    
    @Override
    public Collection<SourceUnit> sources(ArtifactType type) {
        throw new UnsupportedOperationException("Direct source access not supported. Use SourceLibrary directly.");
    }
    
    @Override
    public Optional<SourceUnit> source(ArtifactType type, String id, int version) {
        throw new UnsupportedOperationException("Direct source access not supported. Use SourceLibrary directly.");
    }
    
    @Override
    public boolean hasSources(ArtifactType type) {
        throw new UnsupportedOperationException("Direct source access not supported. Use SourceLibrary directly.");
    }
    
    // Private implementation methods
    
    private void registerFrontendPipelines() {
        registry.register("Rule", new RuleCompilationPipeline());
        registry.register("Ruleset", new RuleSetCompilationPipeline());
        registry.register("EvidenceType", new EvidenceTypeCompilationPipeline());
        registry.register("Task", new TaskCompilationPipeline());
        registry.register("Challenge", new ChallengeCompilationPipeline());
    }
    
    private void registerBackendCompilers() {
        backendCompilers.put(ArtifactType.EvidenceType, this::compileEvidenceType);
        backendCompilers.put(ArtifactType.Rule, this::compileRule);
        backendCompilers.put(ArtifactType.Ruleset, this::compileRuleset);
        backendCompilers.put(ArtifactType.Task, this::compileTask);
        backendCompilers.put(ArtifactType.Challenge, this::compileChallenge);
    }
    
    private List<String> parseZipFile(String zipFilePath) {
        try {
            return ZipFileParser.parseZipFile(zipFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse ZIP file: " + zipFilePath, e);
        }
    }
    
    private List<CompilationResult> compileFrontend(SourceLibrary sourceLibrary) {
        List<CompilationResult> results = new ArrayList<>();
        // Parallel way
        sourceLibrary.allSources().parallelStream().forEach(sourceUnit -> {
             results.add(compiler.compile(sourceUnit.content().toString()));
        });

        
        return results;
    }
    private CompilationContext compileBackend(
            List<CompilationResult> frontendResults,
            CompilationReport.Builder reportBuilder
    ) {
        CompilationContext ctx =
                new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());

        Map<ArtifactType, Integer> compiledCounts = new HashMap<>();

        //Group DSLs by artifact type
        Map<ArtifactType, List<JsonNode>> grouped = new EnumMap<>(ArtifactType.class);

        for (CompilationResult result : frontendResults) {
            if (!result.isSuccess()) continue;

            try {
                JsonNode dsl = MAPPER.readTree((String) result.getArtifact());
                ArtifactType type =
                        ArtifactType.valueOf(dsl.path("kind").asText());

                grouped
                        .computeIfAbsent(type, k -> new ArrayList<>())
                        .add(dsl);

            } catch (Exception e) {
                // Parsing error at backend stage

            }
        }

        // Explicit compilation order
        List<ArtifactType> compilationOrder = List.of(
                ArtifactType.EvidenceType,
                ArtifactType.Rule,
                ArtifactType.Ruleset,
                ArtifactType.Task,
                ArtifactType.Challenge
        );

        for (ArtifactType type : compilationOrder) {
            List<JsonNode> dslNodes = grouped.get(type);
            if (dslNodes == null) continue;

            BiFunction<JsonNode,CompilationContext, CompiledArtifact> compiler =
                    backendCompilers.get(type);

            if (compiler == null) {

                continue;
            }

            for (JsonNode dsl : dslNodes) {
                try {
                    CompiledArtifact compiled = compiler.apply(dsl,ctx);
                    ctx.register(compiled);
                    compiledCounts.merge(type, 1, Integer::sum);
                } catch (CompilationException ce) {
                    LOGGER.error("artifact id {} - error {}",ce.getArtifactId(),ce.getMessage());
                } catch (Exception e) {

                }
            }
        }

        reportBuilder.compiledArtifactCounts(compiledCounts);

        ctx.freeze();
        return ctx;
    }


    private CompilationContext getContext()
    {
        return ctx;
    }
    private CompiledArtifact compileEvidenceType(JsonNode dsl,CompilationContext ctx) {
        EvidenceBackendCompiler compiler = new EvidenceBackendCompiler();
        return compiler.compile(dsl,ctx);
    }
    
    private CompiledArtifact compileRule(JsonNode dsl,CompilationContext ctx) {
        RuleBackendCompiler compiler = new RuleBackendCompiler();
        return compiler.compile(dsl, ctx);
    }
    
    private CompiledArtifact compileRuleset(JsonNode dsl,CompilationContext ctx) {
        RuleSetBackendCompiler compiler = new RuleSetBackendCompiler();
        return compiler.compile(dsl, ctx);
    }
    
    private CompiledArtifact compileTask(JsonNode dsl,CompilationContext ctx) {
        TaskBackendCompiler compiler = new TaskBackendCompiler();
        return compiler.compile(dsl, ctx);
    }
    
    private CompiledArtifact compileChallenge(JsonNode dsl,CompilationContext ctx) {
        ChallengeBackendCompiler compiler = new ChallengeBackendCompiler();
        return compiler.compile(dsl, ctx);
    }
    
    private Map<ArtifactType, Integer> countSourcesByType(SourceLibrary library) {
        return library.getArtifactTypes().stream()
                .collect(Collectors.toMap(
                    type -> type,
                    type -> library.sources(type).size()
                ));
    }
}