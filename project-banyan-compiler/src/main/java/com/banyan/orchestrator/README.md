# Banyan Orchestrator

The Banyan Orchestrator is the central component responsible for managing the complete compilation lifecycle of Banyan DSL artifacts. It follows a well-defined pipeline from source input to compiled DAR output.

## Architecture Overview

The orchestrator implements the canonical compilation flow:

```
ZIP INPUT
   ↓
Source Parsing
   ↓
In-memory Source Library (frozen)
   ↓
Frontend Compilation
   ↓
Backend Compilation
   ↓
CompilationContext (frozen)
   ↓
CompilationOutcome
   ↓
Emitter
   ↓
DAR + Compilation Report
```

## Key Components

### 1. Source Library (`SourceLibrary`)
- **Purpose**: In-memory indexed view of input sources
- **Characteristics**: Immutable after construction, indexed by ArtifactType, id, and version
- **Responsibilities**: Source storage, lookup, and metadata management

### 2. Source Unit (`SourceUnit`)
- **Purpose**: Immutable value object representing a single source artifact
- **Contents**: Parsed JSON content with type, id, version metadata
- **Validation**: Validates required fields during construction

### 3. Orchestrator (`Orchestrator`)
- **Purpose**: Main orchestrator implementing the complete compilation lifecycle
- **Responsibilities**: 
  - Source parsing and library creation
  - Frontend and backend compilation coordination
  - CompilationOutcome building
  - DAR file emission
  - Compilation reporting

### 4. Compilation Report (`CompilationReport`)
- **Purpose**: Immutable snapshot of compilation execution metrics
- **Contents**: Timing information, artifact counts, error/warning statistics
- **Usage**: Performance analysis and debugging

### 5. ZIP File Parser (`ZipFileParser`)
- **Purpose**: Secure ZIP file parsing with Zip Slip protection
- **Features**: Recursive JSON extraction, temporary file management
- **Security**: Validates file paths to prevent directory traversal attacks

## Usage

### Basic Compilation

```java
// Create orchestrator
Orchestrator orchestrator = new Orchestrator();

// Execute compilation
CompilationResult result = orchestrator.orchestrate(
    "/path/to/sources.zip",    // Input ZIP file
    "/path/to/output",         // Output directory  
    ArtifactType.Challenge,    // Root artifact type
    "my_challenge",            // Root artifact ID
    1                          // Root artifact version
);

// Check results
if (result.isSuccess()) {
    Map<String, Object> data = (Map<String, Object>) result.getArtifact();
    CompilationOutcome outcome = (CompilationOutcome) data.get("outcome");
    CompilationReport report = (CompilationReport) data.get("report");
    
    System.out.println("Compilation successful!");
    System.out.println("Report: " + report);
} else {
    System.out.println("Compilation failed:");
    result.getErrors().forEach(System.out::println);
}
```

### Phase-by-Phase Control

The orchestrator can be used to execute individual compilation phases:

```java
// Parse sources only
List<String> jsonSources = ZipFileParser.parseZipFile(zipFilePath);
SourceLibrary library = SourceLibrary.fromJsonSources(jsonSources);

// Compile frontend only
BanyanCompiler compiler = new BanyanCompiler(registry);
List<CompilationResult> frontendResults = new ArrayList<>();
for (SourceUnit source : library.allSources()) {
    frontendResults.add(compiler.compile(source.content().toString()));
}

// Build compilation context
CompilationContext context = compileBackend(frontendResults);
context.freeze();

// Build outcome
CompilationOutcomeBuilder builder = new CompilationOutcomeBuilder(context, root);
CompilationOutcome outcome = builder.build();
```

## Design Principles

### 1. Separation of Concerns
- **Orchestrator**: Manages compilation lifecycle, does not perform compilation
- **Frontend Pipelines**: Handle schema and semantic validation
- **Backend Compilers**: Handle artifact compilation and dependency resolution
- **Emitters**: Handle output generation

### 2. Immutability
- SourceLibrary is immutable after construction
- SourceUnit is an immutable record
- CompilationReport is an immutable snapshot
- CompilationOutcome is immutable

### 3. Controlled Access
- Orchestrator exposes controlled APIs for source access
- SourceLibrary is not passed around directly
- Context is frozen before outcome building

### 4. Error Handling
- Comprehensive error reporting at each phase
- Graceful degradation when possible
- Detailed error messages for debugging

## Security Features

### Zip Slip Protection
The `ZipFileParser` includes protection against Zip Slip attacks:

```java
// Security check: Zip Slip protection
if (!newPath.startsWith(targetDir)) {
    throw new IOException("Entry is outside of the target dir: " + entry.getName());
}
```

### Input Validation
- SourceUnit validates required fields during construction
- SourceLibrary validates JSON parsing
- Orchestrator validates compilation results

## Performance Considerations

### Memory Management
- Temporary directories are cleaned up after ZIP extraction
- SourceLibrary uses efficient indexing structures
- CompilationContext uses HashMap for symbol resolution

### Parallel Processing
The orchestrator is designed to support parallel processing:
- Source parsing can be parallelized
- Frontend compilation can be parallelized
- Backend compilation can be parallelized

## Integration Points

### Frontend Integration
The orchestrator integrates with existing frontend pipelines:

```java
private void registerFrontendPipelines() {
    registry.register("Rule", new RuleCompilationPipeline());
    registry.register("Ruleset", new RuleSetCompilationPipeline());
    registry.register("EvidenceType", new EvidenceTypeCompilationPipeline());
    registry.register("Task", new TaskCompilationPipeline());
    registry.register("Challenge", new ChallengeCompilationPipeline());
}
```

### Backend Integration
The orchestrator integrates with existing backend compilers:

```java
private void registerBackendCompilers() {
    backendCompilers.put(ArtifactType.EvidenceType, this::compileEvidenceType);
    backendCompilers.put(ArtifactType.Rule, this::compileRule);
    backendCompilers.put(ArtifactType.Ruleset, this::compileRuleset);
    backendCompilers.put(ArtifactType.Task, this::compileTask);
    backendCompilers.put(ArtifactType.Challenge, this::compileChallenge);
}
```

## Testing

The orchestrator includes comprehensive testing support:

- Unit tests for individual components
- Integration tests for complete compilation workflow
- Performance tests for large source sets
- Security tests for ZIP parsing

## Future Enhancements

### Planned Features
- Parallel compilation support
- Incremental compilation
- Caching mechanisms
- Advanced error recovery
- Plugin system for custom artifact types

### Performance Optimizations
- Memory-efficient source storage
- Lazy compilation
- Incremental dependency analysis
- Parallel artifact resolution

## Troubleshooting

### Common Issues

1. **ZIP Parsing Errors**: Ensure ZIP files are valid and contain JSON sources
2. **Schema Validation Errors**: Check source JSON against schema definitions
3. **Dependency Resolution Errors**: Verify all referenced artifacts are present
4. **Memory Issues**: Monitor memory usage with large source sets

### Debug Information

The compilation report provides detailed timing and statistics:

```java
CompilationReport report = (CompilationReport) resultMap.get("report");
System.out.println("Total duration: " + report.getTotalDuration());
System.out.println("Source parsing: " + report.getSourceParsingDuration());
System.out.println("Frontend compilation: " + report.getFrontendCompilationDuration());
System.out.println("Backend compilation: " + report.getBackendCompilationDuration());
```

## Contributing

When contributing to the orchestrator:

1. Follow the existing design patterns
2. Maintain immutability where possible
3. Add comprehensive tests for new features
4. Update documentation for API changes
5. Consider performance implications of changes