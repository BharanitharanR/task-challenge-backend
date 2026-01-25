# Banyan Compiler Architecture — Final Design

## Overview

The Banyan compiler is a **multi-phase, graph-aware, dependency-resolving compiler** designed to transform high-level DSLs into deterministic, runtime-ready compiled artifacts.

It is explicitly **not** a validator-only system, nor a runtime interpreter.
It follows classical compiler principles:

* Frontend validation
* Backend lowering
* Contextual dependency resolution
* Deterministic artifact emission

This document describes the **final architecture**, the **design rationale**, and the **roles of core components**.

---

## High-Level Compiler Pipeline

```
┌──────────────────────┐
│      DSL Inputs       │
│──────────────────────│
│ EvidenceType DSL      │
│ Rule DSL              │
│ Ruleset DSL           │
│ Task DSL              │
│ Challenge DSL          │
└──────────┬───────────┘
           │
           ▼
┌────────────────────────────────────────────┐
│           FRONTEND COMPILER                 │
│                                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │ Schema   │→ │ Semantic │→ │ Lint     │ │
│  │ Validate │  │ Validate │  │ (Warn)   │ │
│  └──────────┘  └──────────┘  └──────────┘ │
│                                            │
│  Output: Validated JsonNode                 │
└────────────────────┬───────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────┐
│            BACKEND COMPILER                │
│                                            │
│  ┌──────────────────────────────────────┐ │
│  │       CompilationContext (LIVE)       │ │
│  │                                      │ │
│  │  - Symbol table                       │ │
│  │  - Dependency resolution              │ │
│  │  - Compatibility resolvers            │ │
│  │  - NO persistence / IO                │ │
│  └──────────────────────────────────────┘ │
│                                            │
│ Evidence → Rule → Ruleset → Task → Challenge│
│                                            │
└────────────────────┬───────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────┐
│           CompilationOutcome               │
│      (Frozen, Immutable, Derived)          │
│                                            │
│  - Success / Failure                       │
│  - Error list                              │
│  - Root artifacts                          │
│  - Reachability analysis                   │
│  - Artifact selection (GC-like)            │
│                                            │
└────────────────────┬───────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────┐
│            Artifact Emitters               │
│                                            │
│  FileSystem | Zip | DB | S3 | Memory        │
│                                            │
└────────────────────────────────────────────┘
```

---

## Core Design Principles

### 1. Separation of Concerns (Non-Negotiable)

| Component          | Responsibility                                 |
| ------------------ | ---------------------------------------------- |
| Frontend Compiler  | Validate DSL correctness                       |
| Backend Compiler   | Build compiled artifacts                       |
| CompilationContext | Dependency resolution & compatibility checking |
| CompilationOutcome | Decide *what survives*                         |
| ArtifactEmitter    | Persist bytes only                             |

**No component leaks responsibility upward or downward.**

---

## CompilationContext (Live Working Heap)

### Role

`CompilationContext` represents the **mutable working state** of a compilation run.

> Think of it as the **heap during compilation**.

### Responsibilities

* Register compiled artifacts
* Resolve dependencies by `(type, id, version)`
* Enforce compatibility via resolvers
* Track compilation state (RUNNING / FAILED / COMPLETED)

### Explicit Non-Responsibilities

* ❌ No persistence
* ❌ No artifact emission
* ❌ No final success decision

### Conceptual Model

```
CompilationContext
├── symbolTable
│   ├── EvidenceType:id:version
│   ├── Rule:id:version
│   ├── Ruleset:id:version
│   ├── Task:id:version
│   └── Challenge:id:version
│
├── compatibilityResolvers
│
└── state (RUNNING | FAILED | COMPLETED)
```

---

## Backend Compiler Flow

Each DSL type has a **dedicated backend compiler**:

```
AbstractBackendCompiler<T>
        ▲
        │
┌───────┴────────┐
│ EvidenceBackend│
│ RuleBackend    │
│ RulesetBackend │
│ TaskBackend    │
│ ChallengeBackend│
└────────────────┘
```

### Guarantees

* Input is already schema + semantic validated
* Backend compiler may **resolve dependencies**
* Backend compiler may **fail compilation**
* Backend compiler returns a **CompiledArtifact**

---

## Compatibility System (Compiler-Time Policy)

### Motivation

Rules and EvidenceTypes are **not inherently compatible**.
Compatibility is a **compiler policy**, not DSL semantics.

### Design

* Compatibility is declared externally (JSON / config)
* Hydrated at compiler bootstrap
* Queried during backend compilation

### Interface

```java
interface CompatibilityResolver<A, B> {

    boolean isCompatible(A left, B right);

    Set<B> supportedRight(A left);

    Set<A> supportedLeft(B right);
}
```

### Bootstrap Model

```
CompilerBootstrap
│
├── CompatibilityResolver<RuleType, EvidenceValueType>
├── CompatibilityResolver<TaskResultType, RuleType>
└── (future extensions)
```

No reflection.
No runtime checks.
Pure compile-time enforcement.

---

## CompilationOutcome (The “Garbage Collector”)

### Purpose

`CompilationOutcome` is the **terminal authority** that decides:

* Did compilation succeed?
* What artifacts are reachable?
* What artifacts should be emitted?

### Why It Exists

CompilationContext **cannot know** when it is “done enough”.

CompilationOutcome:

* Takes a frozen context
* Performs reachability analysis
* Eliminates unused artifacts
* Produces a deterministic result

### Reachability (GC Analogy)

```
Roots: Challenge(s)

Challenge
  ↓
Task
  ↓
Ruleset
  ↓
Rule
  ↓
EvidenceType
```

Anything not reachable from a root is **garbage**.

---

## CompilationOutcome Structure

```java
final class CompilationOutcome {

    boolean success;
    List<CompilationException> errors;

    Set<CompiledArtifact<?>> reachableArtifacts;

    ArtifactType rootType;
}
```

### Key Properties

* Immutable
* Serializable
* Replayable
* Deterministic

---

## Artifact Emission

### Philosophy

Artifact emission is **dumb by design**.

Emitters:

* do not validate
* do not resolve
* do not analyze

They only **write bytes**.

### Examples

```java
ArtifactEmitter fsEmitter;
ArtifactEmitter zipEmitter;
ArtifactEmitter inMemoryEmitter;
```

---

## Why CompilationOutcome Must Be Separate

You correctly identified this:

> *“CompilationContext funnels into CompilationOutcome”*

This achieves:

* Isolation of concerns
* Retryable emission
* Multi-target output
* Future incremental compilation

---

## Final Mental Model (Keep This)

| Concept            | Analogy           |
| ------------------ | ----------------- |
| DSL                | Source code       |
| Frontend compiler  | Type checker      |
| Backend compiler   | Lowering phase    |
| CompilationContext | Heap              |
| CompilationOutcome | Garbage collector |
| ArtifactEmitter    | Object serializer |

---

## Status

✅ Frontend compiler complete
✅ Backend compilers largely complete
✅ Compatibility system designed correctly
⏳ CompilationOutcome implementation pending
