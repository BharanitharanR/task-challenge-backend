# Evidence Type Definition DSL

## Overview

An **EvidenceType** defines the **shape of reality** that the platform can reason about.

It specifies:

* What evidence categories exist
* What metrics are available within each category
* The data types, units, and capabilities of those metrics

EvidenceTypes do **not** contain logic. They are a **schema contract** between real-world signals and the rule engine.

---

## Why EvidenceType Exists

EvidenceType acts as:

* A type system for evidence
* A compatibility contract for rules
* A compile-time safety net

Without EvidenceType:

* Rules reference free-form strings
* Errors appear at runtime
* Auditability and trust collapse

---

## Structure

```json
{
  "kind": "EvidenceType",
  "id": "<string>",
  "version": <integer>,
  "status": "DRAFT | ACTIVE | DEPRECATED",
  "spec": {
    "metrics": {
      "<metric_name>": {
        "type": "<DATA_TYPE>",
        "unit": "<UNIT>",
        "capabilities": ["<CAPABILITY>"]
      }
    }
  }
}
```

---

## Field Definitions

### kind

* **Type:** string
* **Value:** Must be `EvidenceType`
* **Purpose:** Identifies the definition as an EvidenceType

---

### id

* **Type:** string
* **Constraints:** Globally unique, immutable across versions
* **Purpose:** Canonical identifier for the evidence category

---

### version

* **Type:** integer
* **Constraints:** Must be ≥ 1; immutable once published
* **Purpose:** Enables versioned evolution of evidence schemas

---

### status

* **Type:** enum
* **Allowed Values:** `DRAFT`, `ACTIVE`, `DEPRECATED`
* **Purpose:** Controls validation strictness and runtime eligibility

---

### spec

Container for evidence schema definitions.

---

#### spec.metrics

A map of metric definitions available under this EvidenceType.

Each key represents a metric name that rules may reference.

---

##### metric.type

* **Type:** enum
* **Allowed Values:** `NUMBER`, `BOOLEAN`, `STRING`, `DURATION`
* **Purpose:** Declares the data type of the metric

---

##### metric.unit

* **Type:** string
* **Purpose:** Declares the measurement unit for the metric
* **Examples:** `SECONDS`, `COUNT`, `KMH`, `BOOLEAN`

---

##### metric.capabilities

* **Type:** array of enum values
* **Examples:**

  * `COMPARABLE`
  * `AGGREGATABLE`
  * `TEMPORAL`
* **Purpose:** Declares what operations rules may perform on this metric

---

## Semantic Constraints (Compiler-Enforced)

These rules are enforced by the ingestion compiler:

* Metric names must be unique within an EvidenceType
* Metric types must be compatible with declared capabilities
* ACTIVE EvidenceTypes must have at least one metric
* DEPRECATED EvidenceTypes cannot be newly referenced

---

## Example: Driving Metrics EvidenceType

```json
{
  "kind": "EvidenceType",
  "id": "DRIVING_METRIC",
  "version": 1,
  "status": "ACTIVE",
  "spec": {
    "metrics": {
      "speed_over_limit_seconds": {
        "type": "NUMBER",
        "unit": "SECONDS",
        "capabilities": ["COMPARABLE", "AGGREGATABLE"]
      },
      "lane_departure_count": {
        "type": "NUMBER",
        "unit": "COUNT",
        "capabilities": ["COMPARABLE"]
      }
    }
  }
}
```

---

## EvidenceType Lifecycle

EvidenceTypes follow a **strict lifecycle** that controls validation and execution.

```
DRAFT → ACTIVE → DEPRECATED
```

### DRAFT

* Forward references allowed
* Structural validation only
* Not eligible for runtime execution

### ACTIVE

* Full semantic validation enforced
* Metrics must be complete and compatible
* Eligible for runtime evaluation

### DEPRECATED

* Remains valid for historical evaluation
* Cannot be referenced by new definitions
* Safe for audits and replay

---

## Relationship to Tasks and Rules

* Tasks declare which EvidenceType they consume
* Rules declare which metric they evaluate
* The compiler enforces compatibility using EvidenceType

EvidenceTypes do **not** reference tasks or rules directly.

---

## One Line to Remember

> **EvidenceType defines facts; rules define logic; the compiler enforces compatibility.**
