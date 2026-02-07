# Unit Test Strategy (Scalable / Change-Resilient)

## Goal
Provide a **scalable** and **change-resilient** unit test design for the Banyan compiler codebase. The strategy favors **spec-driven**, **data-driven**, and **contract-based** tests to minimize test churn when internals evolve.

## Design Principles
1. **Black-box / Contract Focus**
   - Validate *observable behavior* (inputs/outputs/errors) rather than internal implementation.
   - Ensure tests describe *contracts* (error codes, invariants, semantic rules) that should remain stable even as implementations change.

2. **Data-Driven Test Vectors**
   - Use JSON resources in `src/test/resources/**` as canonical test vectors.
   - Build parameterized tests to load fixtures from folders like:
     - `src/test/resources/rule/schema/valid`
     - `src/test/resources/rule/schema/invalid`
     - `src/test/resources/rule/semantic/valid`
     - `src/test/resources/rule/semantic/invalid`
     - And similarly for `challenge`, `ruleset`, `task`, `evidence-type`.

3. **Fixture Builders + Factory Helpers**
   - Create a shared `testutil` package with builders for:
     - JSON nodes (simple helper for small inline JSON),
     - `CompilationContext` (with pre-registered compiled artifacts),
     - `CompiledArtifact` stubs for dependency resolution.
   - Builders allow tests to remain stable when constructors change.

4. **Golden-File Assertions for Serialization**
   - For JSON serialization (`CompiledArtifactSerializer`, `ZipEmitter` outputs), prefer golden-file comparisons stored in `src/test/resources/expected/**`.
   - Compare JSON trees (ignoring ordering) rather than raw text to reduce brittleness.

5. **Dynamic Test Discovery**
   - Use JUnit 5 `@TestFactory` or `@ParameterizedTest` to auto-discover test vectors from resource folders.
   - Adding new JSON examples should automatically expand test coverage without changing test code.

6. **Stable Assertions for Time/Hash Fields**
   - Metadata uses `System.currentTimeMillis()` and SHA-256 hashes.
   - Tests should assert:
     - `compiledAtEpochMillis > 0`,
     - hash is 64 hex chars,
     - compilerVersion equals expected string.
   - Avoid strict equality on timestamps.

7. **Separation of Concerns**
   - **Schema tests** validate JSON schema only.
   - **Semantic tests** validate JSON semantic rules only.
   - **Backend compiler tests** validate compilation outputs and dependency resolution.
   - **Orchestrator tests** validate end-to-end orchestration using stubbed data sources.

## Test Suite Structure (Recommended)
```
src/test/java/com/banyan/compiler/
  core/
  schema/
  semantics/
  lint/
  backend/
  pipeline/
  registry/
  emitter/
src/test/java/com/banyan/orchestrator/
  OrchestratorTest.java
  SourceLibraryTest.java
  ZipFileParserTest.java
src/test/java/com/banyan/compiler/testutil/
  JsonFixtureLoader.java
  CompilationContextBuilder.java
  ArtifactFactory.java
  JsonNodeFactory.java
  AssertionsExt.java
```

## Core Test Utilities (Design)
### JsonFixtureLoader
Loads JSON test vectors from resource folders and returns parameterized test arguments.

### CompilationContextBuilder
Fluent builder to register compiled artifacts in `CompilationContext`.

### ArtifactFactory
Creates minimal compiled artifacts (EvidenceType, Rule, Ruleset, Task, Challenge).

### AssertionsExt
Custom assertions for:
- `CompilationException` (code, artifact metadata)
- `CompilationResult` (success, errors, warnings)
- Metadata hash format

## Success Criteria
- New JSON samples extend coverage **without modifying tests**.
- Tests target contracts (error codes, output shapes), not internals.
- Entire suite runs with **no code changes required**.
