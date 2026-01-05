
# 🌳 Project Banyan

**Project Banyan** is a **metadata-driven rules evaluation platform** built around a compiler + runtime architecture.

At its core, Banyan allows you to **define behavior declaratively (DSLs)**, **compile it into deterministic artifacts**, and **execute it safely at runtime** — without embedding business logic into code.

---

## ✨ Core Philosophy

> **Author behavior as data.
> Compile intent into structure.
> Execute deterministically.**

Banyan separates **what** a system should evaluate from **how** it is evaluated.

---

## 🧱 Fundamental Concepts

### 1️⃣ EvidenceType

Defines **what data exists** and **its shape**.

* Pure metadata
* No thresholds
* No logic
* Versioned and immutable

Example:

```json
{
  "kind": "EvidenceType",
  "id": "LOGIN_ATTEMPT",
  "version": 1,
  "status": "ACTIVE",
  "spec": {
    "fields": [
      { "name": "failedAttempts", "type": "INTEGER", "required": true },
      { "name": "businessHours", "type": "BOOLEAN", "required": true }
    ]
  }
}
```

---

### 2️⃣ Rule

Defines **how evidence is evaluated**.

* Typed predicates
* Parameterized
* Deterministic
* Evaluated in isolation

Example:

```json
{
  "kind": "Rule",
  "id": "max_failed_attempts",
  "version": 1,
  "spec": {
    "type": "THRESHOLD",
    "input": "failedAttempts",
    "operator": "<=",
    "value": 3
  }
}
```

---

### 3️⃣ Ruleset (Upcoming)

A **composition of rules** into logical expressions (AND / OR).

Rulesets do **not** introduce new logic — they only combine rules.

---

### 4️⃣ Task

Associates a **Ruleset** with a semantic unit of evaluation.

A task answers:

> “What outcome are we computing?”

---

### 5️⃣ Challenge

A **runtime-evaluable unit** composed of Tasks.

Challenges:

* Are versioned
* Can be reused
* Are compiled once
* Executed many times

---

## 🏗️ Architecture Overview

Banyan is split into **two strict phases**:

```
Authoring Time (Compiler)        Runtime
---------------------------     -----------------------
DSL (JSON)                      Evidence Input
   ↓                                  ↓
Schema Validation                AST Rehydration
   ↓                                  ↓
Semantic Validation               Deterministic Evaluation
   ↓                                  ↓
Linting (Warnings)               Results
   ↓
AST Serialization
```

---

## 🧠 Compiler Design (Phase 2)

The compiler is **generic and extensible**, using a **registry-driven pipeline model**.

### Key Properties

* No DSL-specific logic in the compiler core
* Each DSL owns its own pipeline
* Schema → Semantics → Lint enforced uniformly
* Deterministic behavior

### Compiler Entry Point

```java
CompilationResult result = compiler.compile(dslJson);
```

---

## 🔌 Compilation Pipelines

Each DSL registers its own pipeline:

```java
registry.register("EvidenceType", new EvidenceTypeCompilationPipeline());
registry.register("Rule", new RuleCompilationPipeline());
```

Each pipeline provides:

* `SchemaValidator`
* `SemanticValidator`
* `Linter`

The compiler orchestrates execution.

---

## 📦 Module Structure

```
banyan/
├── compiler/
│   ├── core/          # Compiler, pipeline contracts
│   ├── registry/      # Pipeline registry
│   ├── schema/        # JSON Schema validators
│   ├── semantics/     # Semantic validators
│   ├── lint/          # Non-blocking lint rules
│   └── pipeline/      # DSL-specific pipelines
│
├── runtime/           # AST execution (Phase 1)
│
├── schemas/           # JSON Schema definitions
│
└── docs/
    └── KNOWN_ISSUES.md
```

---

## ✅ Validation Model

### Schema Validation

* Structural correctness
* JSON Schema (Draft 2020-12)
* Fast failure

### Semantic Validation

* Meaningful correctness
* Determinism guarantees
* Platform safety checks

### Linting

* Non-blocking
* Style and best-practice warnings

---

## 🧪 Testing Philosophy

* Resource-driven tests
* Valid / invalid JSON corpora
* Hands-free testing
* No test logic duplication

Example:

```
src/test/resources/
├── rule/
│   ├── schema-valid/
│   ├── schema-invalid/
│   ├── semantic-valid/
│   └── semantic-invalid/
```

---

## 🔒 What Banyan Explicitly Avoids

* ❌ Hard-coded business logic
* ❌ Dynamic code execution
* ❌ Runtime DSL parsing
* ❌ Hidden coupling between layers
* ❌ “Smart” frameworks or reflection magic

---

## 🧭 Current Status

### Phase 1 — Runtime (Complete)

* AST execution model
* Deterministic rule evaluation

### Phase 2 — Compiler (In Progress)

* ✅ EvidenceType DSL
* ✅ Rule DSL
* ⏳ Ruleset DSL
* ⏳ Challenge DSL
* ⏳ AST serialization

---

## 🎯 Design Goal

> **Make behavior evolvable without redeploying code.**

Banyan is designed for:

* policy engines
* compliance systems
* scoring frameworks
* habit tracking
* evaluation pipelines
* rule-driven products

---

## 🧠 Guiding Principle

> **If runtime code needs to change because of new rules, the design has failed.**

---

## 📌 Known Issues

See [`KNOWN_ISSUES.md`](docs/KNOWN_ISSUES.md) for deferred schema-library quirks and tracked follow-ups.

---

## 🪜 What Comes Next

* Ruleset DSL
* AST Builder
* Serialized AST artifacts
* Runtime rehydration
* Explainability

---

## 🏁 Final Note

Project Banyan is intentionally:

* boring
* explicit
* predictable

That is a feature — not a limitation.
