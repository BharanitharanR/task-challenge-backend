# Detailed Unit Test Cases (Contract-First)

> **Note:** These tests are written to be **scalable** and **change-resilient** by asserting contracts instead of internals. Where possible, use resource-driven parameterization so new cases require no test code changes.

---

## 1) Core Compiler

### 1.1 `BanyanCompiler`
**File:** `src/main/java/com/banyan/compiler/core/BanyanCompiler.java`

**Test Cases**
1. **Missing kind**
   - **Input:** JSON without `kind` or blank `kind`.
   - **Expected:** `CompilationResult.failure` with error `BANYAN_COMPILER_ERROR 00001`.

2. **Unknown pipeline**
   - **Input:** JSON with `kind` not registered in `CompilationPipelineRegistry`.
   - **Expected:** `CompilationResult.failure` with `BANYAN_COMPILER_ERROR 00002`.

3. **Registered pipeline success**
   - **Setup:** Register mock `CompilationPipeline` returning success.
   - **Input:** Valid DSL JSON with matching `kind`.
   - **Expected:** `CompilationResult.success` with warnings preserved.

4. **Pipeline error passthrough**
   - **Setup:** Pipeline returns `CompilationResult.failure`.
   - **Expected:** Errors are returned unchanged.

5. **Invalid JSON**
   - **Input:** malformed JSON.
   - **Expected:** `CompilationResult.failure` with `BANYAN_COMPILER_ERROR 00000`.

### 1.2 `CompilationPipeline`
**File:** `src/main/java/com/banyan/compiler/core/CompilationPipeline.java`

**Test Cases**
1. **Schema error short-circuit**
   - **Setup:** schema validator returns error list.
   - **Expected:** semantic/lint not executed, failure returned.

2. **Semantic error short-circuit**
   - **Setup:** schema ok, semantic error list returned.
   - **Expected:** lint not executed, failure returned.

3. **Lint warnings collected**
   - **Setup:** schema + semantic ok, lint returns warnings.
   - **Expected:** success with warnings list.

### 1.3 `CompilationResult`
**File:** `src/main/java/com/banyan/compiler/core/CompilationResult.java`
**Test Cases**
1. `success()` sets `success=true`, `errors=[]`, `warnings` preserved.
2. `failure()` sets `success=false`, `warnings=[]`, `artifact=null`.

---

## 2) Schema Validators

### 2.1 `SchemaValidatorImpl`
**File:** `src/main/java/com/banyan/compiler/schema/SchemaValidatorImpl.java`
**Test Cases**
1. **Valid JSON -> no errors** using a known valid fixture.
2. **Invalid JSON -> schema errors** using invalid fixture.
3. **Invalid JSON syntax** -> returns `{SCHEMA_TYPE}_PARSE_ERROR`.
4. **Missing schema resource** -> throws `IllegalStateException`.

### 2.2 Specific Validators
**Files:**
- `ChallengeSchemaValidator`, `RuleSchemaValidator`, `RuleSetSchemaValidator`, `TaskSchemaValidator`, `EvidenceTypeSchemaValidator`

**Test Cases**
1. **Smoke test** for each validator using one valid fixture in `src/test/resources/**/schema/valid`.
2. **Negative test** using invalid fixtures in `src/test/resources/**/schema/invalid`.

---

## 3) Semantic Validators

### 3.1 `ChallengeSemanticValidator`
**File:** `src/main/java/com/banyan/compiler/semantics/ChallengeSemanticValidator.java`

**Test Cases**
1. **Missing spec** -> `CH_ERR_50000`.
2. **No tasks array / empty** -> `CH_ERR_50001`.
3. **Task ref not object** -> `CH_ERR_50002`.
4. **Duplicate task IDs** -> `CH_ERR_50003`.
5. **Invalid version <= 0** -> `CH_ERR_50006`.
6. **Spec contains actions/expression/operator** -> `CH_ERR_50004`.
7. **Valid sample** -> empty errors.

### 3.2 `RuleSemanticValidator`
**File:** `src/main/java/com/banyan/compiler/semantics/RuleSemanticValidator.java`

**Test Cases**
1. **Missing/invalid spec** -> `RULE_ERR 20000`.
2. **Invalid type** -> `RULE_ERR 20001`.
3. **Missing input** -> `RULE_ERR 20002`.
4. **Missing operator** -> `RULE_ERR 20003`.
5. **Numeric value with non-numeric operator** -> `RULE_ERR 20004`.
6. **Text/boolean with non-equality operator** -> `RULE_ERR 20005`.
7. **Unsupported value type (object/array)** -> `RULE_ERR 20006`.
8. **Valid samples** -> empty errors.

### 3.3 `RuleSetSemanticValidator`
**File:** `src/main/java/com/banyan/compiler/semantics/RuleSetSemanticValidator.java`

**Test Cases**
1. **Missing expression** -> `RULESET_ERR 30001`.
2. **RuleRef not string/blank** -> `RULESET_ERR 30002`.
3. **Invalid operator** -> `RULESET_ERR 30003`.
4. **Operator with < 2 operands** -> `RULESET_ERR 30004`.
5. **Invalid node structure** -> `RULESET_ERR 30005`.
6. **Valid nested expressions** -> empty errors.

### 3.4 `TaskSemanticValidator`
**File:** `src/main/java/com/banyan/compiler/semantics/TaskSemanticValidator.java`

**Test Cases**
1. **Missing/invalid rulesetRef** -> `TASK_ERR 40001`.
2. **rulesetRef.id invalid** -> `TASK_ERR 40009`.
3. **Missing resultType** -> `TASK_ERR 40002`.
4. **Invalid resultType** -> `TASK_ERR 40003`.
5. **Actions not array** -> `TASK_ERR 40004`.
6. **Action on invalid** -> `TASK_ERR 40005`.
7. **Emit not string** -> `TASK_ERR 40006`.
8. **Invalid action field name** -> `TASK_ERR 40007`.
9. **Valid samples** -> empty errors.

### 3.5 `EvidenceTypeSemanticValidator`
**File:** `src/main/java/com/banyan/compiler/semantics/EvidenceTypeSemanticValidator.java`

**Test Cases**
1. **Duplicate field names** -> `EVIDENCE_TYPE_ERR 10001`.
2. **Invalid field type** -> `EVIDENCE_TYPE_ERR 10002`.
3. **No required fields** -> `EVIDENCE_TYPE_ERR 10003`.
4. **Valid samples** -> empty errors.

---

## 4) Linting

### 4.1 `RuleLinter`
**File:** `src/main/java/com/banyan/compiler/lint/RuleLinter.java`

**Test Cases**
1. **Returns empty lint list** for any JSON.

### 4.2 `ChallengeLinter`, `RuleSetLinter`, `TaskLinter`
**Files:** `ChallengeLinter`, `RuleSetLinter`, `TaskLinter`

**Test Cases**
1. **Returns one lint finding** with expected code/message.

---

## 5) Backend Compilers

### 5.1 `AbstractBackendCompiler`
**File:** `src/main/java/com/banyan/compiler/backend/spi/AbstractBackendCompiler.java`

**Test Cases**
1. **readId/readVersion** reads fields correctly from JSON.
2. **metadata** returns:
   - `compilerVersion == "banyan-compiler-2.0"`
   - `compiledAtEpochMillis > 0`
   - `contentHash` length 64 hex chars.

### 5.2 `EvidenceBackendCompiler`
**File:** `src/main/java/com/banyan/compiler/backend/evidence/EvidenceBackendCompiler.java`

**Test Cases**
1. **Fields mapped by name** from `spec/fields`.
2. **ArtifactReference contains EvidenceType self-reference**.
3. **Value types mapped to EvidenceValueType enum**.

### 5.3 `RuleBackendCompiler`
**File:** `src/main/java/com/banyan/compiler/backend/rule/RuleBackendCompiler.java`

**Test Cases**
1. **Missing evidence type in context** -> `CompilationException` MISSING_DEPENDENCY (via context).
2. **Input field missing in evidence type** -> INTERNAL_COMPILER_ERROR.
3. **Type incompatibility** -> CONTEXT_CORRUPTED.
4. **Value coercion types**: int/long/double/boolean/text => correct value type.
5. **Unsupported value type** -> `IllegalStateException`.
6. **Dependencies list includes evidence reference**.

### 5.4 `RuleSetBackendCompiler`
**File:** `src/main/java/com/banyan/compiler/backend/ruleset/RuleSetBackendCompiler.java`

**Test Cases**
1. **Explicit expression parsing**
   - RuleRef leaf resolves dependency.
2. **Implicit ruleRef** (no expression) creates RuleRefNode.
3. **Missing rule dependency** -> MISSING_DEPENDENCY.
4. **Invalid expression structure** -> IllegalStateException.
5. **Dependency list includes all referenced rules**.

### 5.5 `TaskBackendCompiler`
**File:** `src/main/java/com/banyan/compiler/backend/task/TaskBackendCompiler.java`

**Test Cases**
1. **Missing spec** -> INTERNAL_COMPILER_ERROR.
2. **Ruleset dependency missing** -> MISSING_DEPENDENCY.
3. **Actions list compiled** into TaskActionRecord list.
4. **Dependencies include ruleset reference**.

### 5.6 `ChallengeBackendCompiler`
**File:** `src/main/java/com/banyan/compiler/backend/challenge/ChallengeBackendCompiler.java`

**Test Cases**
1. **Missing spec** -> INTERNAL_COMPILER_ERROR.
2. **Missing referenced task** -> MISSING_DEPENDENCY.
3. **CompiledTaskRefs list** created in order.
4. **Dependencies include task references**.

---

## 6) Backend API & Outcome

### 6.1 `CompilationContext`
**File:** `src/main/java/com/banyan/compiler/backend/context/CompilationContext.java`

**Test Cases**
1. **Register & resolve** returns artifact.
2. **Resolve missing** -> MISSING_DEPENDENCY exception.
3. **Freeze prevents register** -> CONTEXT_FREEZE exception.
4. **Compatibility resolver pass-through** to bootstrap.

### 6.2 `CompilationOutcomeBuilder`
**File:** `src/main/java/com/banyan/compiler/backend/outcome/CompilationOutcomeBuilder.java`

**Test Cases**
1. **Root missing** -> failure outcome with error list.
2. **Graph traversal reaches all deps** -> reachable set size.
3. **Missing dependency during walk** -> errors list contains exception.
4. **Cycle handling** -> visited prevents infinite loops.

### 6.3 `CompilationOutcome`
**File:** `src/main/java/com/banyan/compiler/backend/outcome/CompilationOutcome.java`

**Test Cases**
1. `isSuccess()` true when errors empty.
2. `errors()` returns same list.

### 6.4 `CompilationRoot`
**File:** `src/main/java/com/banyan/compiler/backend/outcome/CompilationRoot.java`

**Test Cases**
1. `dependencies()` returns empty list.

### 6.5 `CompiledArtifactSerializer`
**File:** `src/main/java/com/banyan/compiler/backend/api/CompiledArtifactSerializer.java`

**Test Cases**
1. JSON contains `id`, `version`, `artifactType`, `metadata`, `dependencies`, `payload`.
2. Payload serialized correctly for record types.

---

## 7) Pipelines & Registry

### 7.1 `CompilationPipelineRegistry`
**File:** `src/main/java/com/banyan/compiler/registry/CompilationPipelineRegistry.java`

**Test Cases**
1. `register` then `get` returns pipeline.
2. `get` missing returns null.

### 7.2 Compilation pipelines
**Files:**
- `RuleCompilationPipeline`
- `RuleSetCompilationPipeline`
- `EvidenceTypeCompilationPipeline`
- `TaskCompilationPipeline`
- `ChallengeCompilationPipeline`

**Test Cases**
1. Each pipeline returns the expected validator/linter instance types.
2. Validators are non-null.

---

## 8) Orchestrator & Source Library

### 8.1 `SourceLibrary`
**File:** `src/main/java/com/banyan/orchestrator/SourceLibrary.java`

**Test Cases**
1. **fromJsonSources** builds library from valid JSON.
2. **invalid JSON** throws IllegalArgumentException.
3. `sources(type)` returns collection for type.
4. `source(type,id,version)` returns Optional.
5. `hasSources(type)` reflects availability.
6. `size()` equals number of unique SymbolKey entries.

### 8.2 `ZipFileParser`
**File:** `src/main/java/com/banyan/orchestrator/ZipFileParser.java`

**Test Cases**
1. **Parses zip** of sample inputs.
2. **Zip Slip protection** with malicious entry -> IOException.
3. **Non-JSON files ignored**.

### 8.3 `Orchestrator`
**File:** `src/main/java/com/banyan/orchestrator/Orchestrator.java`

**Test Cases**
1. **Frontend errors short-circuit** (schema invalid) -> CompilationResult.failure.
2. **Backend success path** -> outcome success and emission called.
3. **Missing backend compiler** -> continues without crash.
4. **Invalid JSON in backend stage** -> ignored without crash.
5. **Compilation report** contains durations and counts.
6. **OrchestratorContext methods** throw UnsupportedOperationException.

### 8.4 `OrchestratorDemo` (Input-driven tests)
**File:** `src/main/java/com/banyan/orchestrator/OrchestratorDemo.java`

> These tests should focus on reading input ZIP files and validating expected outcomes. Use fixtures in `src/test/resources` and ensure tests are resilient to internal implementation changes.

**Test Cases**
1. **Happy path – banyan-sources.zip**
   - **Input:** `src/test/resources/banyan-sources.zip`
   - **Root:** `ArtifactType.Challenge`, `id="unique_task_challenge"`, `version=1`
   - **Expected:** `CompilationResult.success` and a `CompilationOutcome` with reachable artifacts > 0.
   - **Validate:** `target/compilation_package.dar` exists after emission.

2. **Happy path – sample_challenge_source_zip/challenge_source.zip**
   - **Input:** `src/test/resources/sample_challenge_source_zip/challenge_source.zip`
   - **Root:** `ArtifactType.Challenge`, `id="unique_task_challenge"`, `version=1`
   - **Expected:** `CompilationResult.success`, `CompilationReport.state == COMPLETED`.

3. **Invalid ZIP path**
   - **Input:** non-existent zip file path.
   - **Expected:** `CompilationResult.failure` with `Orchestration failed` message.

4. **Invalid root ID**
   - **Input:** valid ZIP, but rootId not present in sources.
   - **Expected:** `CompilationResult.failure` with missing dependency error in outcome.

5. **Invalid root type**
   - **Input:** valid ZIP, rootType not present in library (e.g., `ArtifactType.Task`).
   - **Expected:** `CompilationResult.failure` due to root resolution failure.

6. **Validate report timings**
   - **Input:** valid ZIP.
   - **Expected:** report durations are non-null and `totalDuration >= 0`.

7. **OrchestratorDemo stdout contract**
   - **Input:** valid ZIP.
   - **Expected:** contains “Compilation successful!” and prints report/outcome.
   - **Note:** Use a stream capture to keep test stable.

---

## 9) Emitters

### 9.1 `ZipEmitter`
**File:** `src/main/java/com/banyan/compiler/backend/emitter/ZipEmitter.java`

**Test Cases**
1. `supports()` returns false (documented behavior).
2. `emit()` creates `target/compilation_package.dar` for successful outcome.
3. `emit()` no-op on failure outcome.
4. Generated `manifests.json` includes expected file list.

---

## 10) Enums & Utility Types

### 10.1 `ArtifactType`
**File:** `src/main/java/com/banyan/compiler/enums/ArtifactType.java`

**Test Cases**
1. `contains("EvidenceType")` should be **true**. (Currently it checks `TaskActionOn`; verify intended behavior.)
2. `contains("Unknown")` should be **false**.

---

## Test Data Sources
Use existing resources under `src/test/resources/**` and existing sample zip files.

***

## Notes on Scalability
- All schema/semantic tests should be parameterized with resource discovery.
- Backend compiler tests should use fixture builders with minimal DSL JSON to keep tests independent of schema changes.

***

## Potential Issues To Track
See `docs/testquery.md` for any blocked/failed cases.
