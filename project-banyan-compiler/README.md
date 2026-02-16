# Banyan Compiler

The **Banyan Compiler** is a multi-phase compiler that transforms Banyan DSL inputs into
deterministic, runtime-ready compiled artifacts (DARs). It performs validation, dependency
resolution, compatibility enforcement, and artifact emission. The compiler is intentionally
separated into a frontend (validation) and backend (lowering + linking) to keep runtime semantics
out of the runtime itself.

## Goals

- **Deterministic compilation**: same inputs → same compiled artifacts.
- **Strict separation of concerns** between frontend validation and backend lowering.
- **Compiler-enforced policy** for rule/evidence compatibility.
- **Reusable artifacts** that can be emitted to ZIP/DAR or other targets.

## Compiler Pipeline (High Level)

```
DSL Inputs → Frontend Validation → Backend Compilation → CompilationOutcome → ArtifactEmitter
```

### Frontend

- Schema validation
- Semantic validation
- Linting and warnings

### Backend

- Dependency resolution through `CompilationContext`
- Compatibility enforcement via `CompatibilityResolver`
- Ordered compilation (Evidence → Rule → Ruleset → Task → Challenge)

### Outcome + Emission

- `CompilationOutcome` performs reachability analysis
- `ArtifactEmitter` persists compiled artifacts (e.g., `ZipEmitter`)

## Key Components

| Area | Key Types | Responsibility |
| --- | --- | --- |
| Orchestration | `Orchestrator` | Coordinates source parsing, frontend, backend, and emission. |
| Frontend pipelines | `CompilationPipelineRegistry` | Wires DSL-specific validation pipelines. |
| Backend context | `CompilationContext` | Mutable compilation heap and symbol table. |
| Backend compilers | `RuleBackendCompiler`, `RuleSetBackendCompiler`, etc. | Lower DSLs into compiled artifacts. |
| Compatibility policy | `CompatibilityResolver` | Enforces rule/evidence compatibility. |
| Artifact emission | `ZipEmitter`, `ArtifactEmitter` | Emits DAR/ZIP artifacts. |

## Usage (Orchestrator)

```java
Orchestrator orchestrator = new Orchestrator();
CompilationResult result = orchestrator.orchestrate(
    "/path/to/banyan-sources.zip",
    "target",
    ArtifactType.Challenge,
    "challenge_id",
    1
);

if (!result.isSuccess()) {
    System.out.println("Compilation failed: " + result.getErrors());
}
```

## Build & Test

```bash
./mvnw -pl project-banyan-compiler -am clean package
./mvnw -pl project-banyan-compiler test
```

## References

- Compiler architecture design: [`docs/BANYAN_COMPILER_ARCHITECTURE_DESIGN.md`](../docs/BANYAN_COMPILER_ARCHITECTURE_DESIGN.md)
- Compiler policy (compatibility matrix): [`docs/COMPILER_POLICY.md`](../docs/COMPILER_POLICY.md)

## Non-Negotiable Principles

1. Compiler owns all semantics and compatibility policy.
2. Runtime never performs semantic validation.
3. Compilation is deterministic and reproducible.
