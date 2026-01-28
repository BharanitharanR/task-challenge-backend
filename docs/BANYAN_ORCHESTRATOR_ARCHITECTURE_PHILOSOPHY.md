# 1️⃣ Orchestrator — “DONE” Checklist ✅

The orchestrator is **DONE** when *all* of the following are true.

---

## A. Input & Source Handling

* [ ] Accepts **exactly one source zip** as input
* [ ] Unzips sources into an **in-memory source library**
* [ ] No pipeline or compiler reads files directly from disk
* [ ] Source parsing errors are captured in the compilation report
* [ ] Source library lifecycle is owned entirely by the orchestrator

**Exit test:**

> Delete the extracted files immediately after unzip — compilation still works.

---

## B. Artifact Type Registration

* [ ] Artifact types are registered centrally (EvidenceType, Rule, Ruleset, Task, Challenge, …)
* [ ] Each artifact type is explicitly marked as:

    * `FRONTEND`
    * `BACKEND`
    * or `BOTH`
* [ ] Each artifact type declares:

    * ordering requirement (`ORDERED` / `UNORDERED`)
    * dependency requirements (if any)

**Exit test:**

> Adding a new artifact type requires **no changes** to orchestrator logic.

---

## C. Compilation Execution Model

### Backend (Ordered)

* [ ] Backend artifact compilation is **strictly ordered**
* [ ] Order is deterministic and dependency-driven
* [ ] A failure in a backend artifact:

    * blocks dependent backend artifacts
    * is recorded in the compilation report
* [ ] Backend compilation is **single-threaded per sequence**

### Frontend (Unordered)

* [ ] Frontend artifact compilation:

    * has **no ordering**
    * runs **in parallel**
* [ ] Failures in frontend artifacts:

    * do NOT block other frontend artifacts
    * do NOT affect backend compilation
* [ ] Parallelism is controlled by orchestrator (ExecutorService)

**Exit test:**

> Frontend artifacts compile correctly even when execution order changes.

---

## D. In-Memory Artifact Library

* [ ] Artifact library is **internal only**
* [ ] Artifacts are stored by:

    * artifact type
    * compilation status
* [ ] Library exposes **read-only access** via orchestrator APIs
* [ ] No pipeline shares artifacts directly with another pipeline

**Exit test:**

> Removing direct artifact passing between pipelines does not break compilation.

---

## E. Compilation Report (Ledger)

* [ ] Compilation report is always produced (success or failure)
* [ ] Report includes:

    * per-artifact-type success count
    * per-artifact-type failure count
    * ordered backend failures
    * frontend failures (non-blocking)
    * warnings
* [ ] Report clearly explains **why a DAR was or wasn’t produced**

**Exit test:**

> A human can answer “what failed and why?” using only the report.

---

## F. Frontend / Backend Registration

* [ ] Registration is done **only by the orchestrator**
* [ ] Backend registration happens only if backend compilation succeeds
* [ ] Frontend registration happens independently
* [ ] Partial success is explicitly supported:

    * frontend success + backend failure
    * backend success + frontend warnings

**Exit test:**

> A caller can distinguish partial success vs full success.

---

## G. DAR Packaging

* [ ] DAR is built **only from the artifact library**
* [ ] DAR format is deterministic
* [ ] DAR includes:

    * compiled artifacts
    * manifest
    * compilation metadata
* [ ] DAR version is explicitly set

**Exit test:**

> Same input zip → byte-for-byte identical DAR.

---

## H. Orchestrator API Contract

* [ ] Single public entry point:

    * `compileAndPackage(zip)`
* [ ] Caller receives:

    * DAR (optional)
    * CompilationReport (always)
* [ ] No internal types (`ArtifactLibrary`, `SourceLibrary`) are exposed

**Exit test:**

> Runtime can be built using only the orchestrator output contract.

---

### If all boxes are checked → **STOP. MOVE TO RUNTIME.**

---

# 2️ Orchestrator Architecture Document 

## 2.1 Responsibility Boundary

> The Orchestrator is the **compiler driver** of Banyan.
> It owns source ingestion, compilation execution, artifact organization, registration, and packaging.

It is **not**:

* a runtime
* a service
* a scheduler
* a persistence layer

---

## 2.2 High-Level Flow

```
Source Zip
   ↓
In-Memory Source Library
   ↓
Artifact Compilation
   ├── Backend (ordered, sequential)
   └── Frontend (unordered, parallel)
   ↓
In-Memory Artifact Library
   ↓
Registration (FE / BE)
   ↓
DAR Packaging
   ↓
Orchestration Result
```

---

## 2.3 Frontend vs Backend Compilation Model

### Backend Compilation (Ordered)

**Characteristics**

* Dependency-sensitive
* Deterministic ordering
* Sequential execution

**Examples**

* EvidenceType
* Rule
* Ruleset
* Task
* Challenge

**Why ordered?**

* Later artifacts depend on earlier ones
* Failure must block downstream compilation
* Required for semantic correctness

---

### Frontend Compilation (Unordered)

**Characteristics**

* No dependencies
* No ordering guarantees
* Parallel execution

**Examples**

* UI metadata
* Visual rules
* Frontend validation artifacts

**Why parallel?**

* No semantic dependencies
* Improves performance
* Failures are isolated

---

## 2.4 Execution Strategy

```text
Phase 1: Backend Compilation (single-threaded)
Phase 2: Frontend Compilation (multi-threaded)
Phase 3: Registration
Phase 4: DAR Packaging
```

> Backend ALWAYS completes before frontend registration and packaging.

---

## 2.5 Internal Components

### Orchestrator

* Owns lifecycle
* Controls execution strategy
* Exposes public API

### Source Library (internal)

* Parsed source representation
* Immutable after creation

### Artifact Library (internal)

* Stores compiled artifacts
* Indexed by artifact type
* Records success/failure

### Compilation Report

* Ledger of everything that happened
* Returned to caller

---

## 2.6 Failure Semantics

| Scenario              | Result             |
| --------------------- | ------------------ |
| Backend failure       | No DAR             |
| Frontend failure only | DAR allowed        |
| Warnings only         | DAR allowed        |
| Mixed FE/BE failure   | Report shows split |

Failures are **never silent**.

---

## 2.7 Runtime Boundary

Runtime:

* Consumes DAR
* Never recompiles
* Never reorders
* Never interprets compiler intent

> Runtime trusts the orchestrator completely.

---

## Final Architectural Truth

> If the orchestrator is correct,
> runtime can be dumb — and that’s a feature, not a bug.

