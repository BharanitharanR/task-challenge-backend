# Banyan Runtime — MVP Design

## 1. Purpose

The Banyan Runtime executes **compiled governance artifacts (DAR)** deterministically.

It evaluates a **Challenge** against supplied **Evidence** and produces a **replayable result**.

The runtime:

* Never sees DSL
* Never performs semantic validation
* Never mutates state
* Never infers meaning

All interpretation belongs to the compiler.

---

## 2. Non-Negotiable Invariants (MVP + Future)

These invariants apply to **all runtime versions**.

1. **DAR is the only executable input**
2. **Evaluation is a pure function**

   ```
   (DAR, ChallengeId, Evidence) → Result
   ```
3. **Execution is deterministic**
4. **Runtime is stateless**
5. **Runtime does not interpret governance semantics**

Breaking any of these invalidates Banyan’s audit guarantees.

---

## 3. What “Runtime” Means in MVP

In MVP, **runtime is an in-process library**, not a service.

* Single JVM
* Shared immutable memory
* Request-scoped execution state only

The runtime is designed to be:

* Embeddable
* Testable
* Replayable

---

## 4. MVP Runtime Architecture

### 4.1 High-Level Structure

```
banyan-runtime-mvp
│
├── DarRuntimeContext
├── Evaluator
└── BanyanRuntime (Facade)
```

This is a **deliberate collapse** of the full design to enable fast validation.

---

## 5. MVP Components

### 5.1 DarRuntimeContext

**Responsibility**

* Load and materialize DAR
* Build immutable AST
* Provide ID-based access to challenges

**Characteristics**

* Created once
* Immutable
* Shared by reference

```java
public final class DarRuntimeContext {

    private final Map<String, ChallengeNode> challenges;

    public ChallengeNode getChallenge(String challengeId) {
        // fail fast if missing
    }
}
```

**Out of Scope**

* Semantic validation
* Compatibility checks
* Mutation or caching

---

### 5.2 Evaluator

**Responsibility**

* Execute AST deterministically
* Bind evidence inline
* Produce evaluation result and trace

**Contract**

```java
public final class Evaluator {

    public EvaluationResult evaluate(
        ChallengeNode challenge,
        Map<String, Object> evidence
    );
}
```

**Rules**

* Evidence is assumed compiler-validated
* Missing evidence → hard failure
* No coercion or defaults
* No IO, no clocks, no randomness

---

### 5.3 BanyanRuntime (Facade)

**Responsibility**

* Orchestrate evaluation flow
* Expose single runtime API

```java
public final class BanyanRuntime {

    private final DarRuntimeContext context;
    private final Evaluator evaluator;

    public EvaluationResponse evaluate(EvaluationRequest request);
}
```

**Important**

* Contains **no business logic**
* Owns call order only

---

## 6. MVP Evaluation Flow

```
EvaluationRequest
     |
     v
BanyanRuntime
     |
     v
DarRuntimeContext → ChallengeNode
     |
     v
Evaluator
     |
     v
EvaluationResponse
```

All components operate on shared immutable DAR.

---

## 7. MVP Example

### Input

```json
{
  "challengeId": "AI_ACCESS_CONTROL",
  "evidence": {
    "deployment_env": "prod",
    "approval_count": 1
  }
}
```

### Output

```json
{
  "challengeId": "AI_ACCESS_CONTROL",
  "result": "FAIL",
  "trace": [
    "Equals(deployment_env, prod) -> true",
    "GTE(approval_count, 2) -> false",
    "AND -> false"
  ]
}
```

This trace + DAR + evidence is sufficient for replay.

---

## 8. Explicit MVP Omissions (Intentional)

The following are **not required** for MVP:

* Separate EvidenceBinder module
* Formal error taxonomy
* TraceBuilder abstraction
* Module-level separation
* Optimized indexing
* Service deployment
* Multi-tenant concerns

These are deferred, not ignored.

---

## 9. What Runtime Will Eventually Become (Post-MVP)

After MVP validation, runtime will evolve into a **componentized in-process system**.

### Target Runtime Component Model

```
RuntimeState (shared immutable)
│
├── DarLoader
├── ArtifactIndex
├── AstMaterializer
│
├── ChallengeResolver
├── EvidenceBinder
├── Evaluator
├── TraceBuilder
│
└── RuntimeFacade
```

### Key Evolutions

| Area         | MVP            | Future Runtime                 |
| ------------ | -------------- | ------------------------------ |
| DAR handling | Single context | Loader + index                 |
| Evidence     | Inline Map     | Typed EvidenceContext          |
| Evaluation   | Inline logic   | Node-specific evaluators       |
| Trace        | String list    | Structured deterministic trace |
| Errors       | Exceptions     | Closed error taxonomy          |
| Packaging    | Single module  | Multiple runtime modules       |
| Language     | Java           | Java + possible Rust core      |

---

## 10. Evolution Principles (Important)

Future changes **must not**:

* Introduce runtime semantics
* Introduce persistence
* Break determinism
* Require recompilation of DAR

Runtime evolution is **additive and structural**, never interpretive.

---

## 11. Definition of MVP Success

The MVP runtime is successful if:

> The same DAR and evidence always produce the same result and trace, indefinitely.

Performance, packaging, and APIs are secondary.

---

## 12. Next Steps After MVP

1. Run real compiled DARs through MVP runtime
2. Identify natural seams for modularization
3. Introduce structured trace
4. Split runtime into formal modules

Only refactor **after** real usage feedback.
