# Test Queries (Chronological)

This file captures questions or issues discovered while designing unit tests **without changing the codebase**. Each entry records the query, what was attempted, and what failed. Entries are listed in chronological order.

---

## 1) ArtifactType.contains behavior

**Query:** Should `ArtifactType.contains(String value)` check the `ArtifactType` enum values or `TaskActionOn`? Current implementation delegates to `TaskActionOn.valueOf(value)`.

**What I did:** While enumerating unit test cases, I added a test for `ArtifactType.contains("EvidenceType")` expecting `true` and `ArtifactType.contains("Unknown")` expecting `false`.

**What failed:** The current implementation would return `false` for `"EvidenceType"` (unless it happens to match `TaskActionOn`), which may indicate a bug or a misnamed method. Confirmation needed before writing test assertions.

---

## 2) OrchestratorDemo root IDs in sample zip files (resolved)

**Query:** What is the correct root ID/version for `banyan-sources.zip` and `sample_challenge_source_zip/challenge_source.zip`? OrchestratorDemo uses `unique_task_challenge@1`, but the sample archives may not contain that root.

**What I did:** Inspected `banyan-sources.zip` and `challenge_source.zip` contents via `unzip -p` to find actual challenge IDs and versions.

**What failed:** The `banyan-sources.zip` challenge lacks an explicit `version` field (defaults to `1`), and its `spec.tasks` uses string IDs rather than `{id,version}` objects. This likely fails schema/semantic validation, so the orchestrator test for this ZIP expects failure (documented in `OrchestratorDemoTest`).

---

## 3) GreetingCommandTest fails due to missing Quarkus TopCommand bean

**Query:** Should `GreetingCommandTest` be disabled or should a `@TopCommand` bean be registered for tests?

**What I did:** Ran `./mvnw test` and observed Quarkus startup failure.

**What failed:** Quarkus could not resolve a `@TopCommand` bean (UnsatisfiedResolutionException), causing `GreetingCommandTest` to fail on launch and argument tests.

---

## 4) Backend compiler tests missing dependencies in context

**Query:** Should backend compiler tests register required dependencies (EvidenceTypes for Rules, Rules for Rulesets, Rulesets for Tasks) or use fixture data that includes them?

**What I did:** Ran the existing backend tests.

**What failed:**
- `RuleBackendCompilerTest` failed because `CONSOLIDATED_EVIDENCES@1` was not registered.
- `RulesetBackendCompilerTest` failed because referenced rules (e.g., `max_failed_attempts@1`) were missing.
- `TaskBackendCompilerTest` failed because referenced ruleset `basic_ruleset@1` was missing.

---

## 5) Ruleset schema validation failures for “valid” fixtures

**Query:** Should the ruleset schema allow `spec.expression.ruleRef` properties in leaf nodes? The schema currently rejects them.

**What I did:** Ran `RulesetSchemaValidatorTest` against fixtures under `ruleset/schema/valid`.

**What failed:** Validation errors like `$.spec.expression.ruleRef: is not defined in the schema and the schema does not allow additional properties` caused failures.

---

## 6) Frontend compiler failures on valid Task/Rule fixtures

**Query:** Are Task and Rule “valid” fixtures expected to pass front-end compilation? They may not meet schema/semantic requirements (e.g., missing rule value, evidence type ref, etc.).

**What I did:** Ran `BanyanCompilerTest` with `task/schema/valid` and `rule/schema/valid` fixtures.

**What failed:** Tests `allValidTaskShouldPass` and `allValidRulesShouldPass` failed due to compilation errors for those “valid” files.
