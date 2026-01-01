# Rule Definition DSL

## Overview

A **Rule** defines **how evidence is evaluated** within the platform.

Rules are the smallest executable units in the system and are designed to be:

* Typed
* Parameterized
* Deterministic
* Reusable
* Immutable once versioned

Rules contain **no executable code** and are evaluated by the framework’s rule engine.

---

## Structure

```json
{
  "kind": "Rule",
  "id": "<string>",
  "version": <integer>,
  "spec": {
    "type": "<string>",
    "input": "<string>",
    "operator": "<operator>",
    "value": "<literal>"
  }
}
```

---

## Field Definitions

### kind

* **Type:** string
* **Value:** Must be `"Rule"`
* **Purpose:** Identifies the definition as a Rule

---

### id

* **Type:** string
* **Constraints:**

  * Alphanumeric characters, underscores, hyphens
  * Must be globally unique
* **Purpose:** Canonical identifier for the rule

---

### version

* **Type:** integer
* **Constraints:**

  * Must be ≥ 1
  * Immutable once published
* **Purpose:** Enables versioned evolution of rules without breaking historical evaluations

---

### spec

Container for rule behavior parameters.

---

#### spec.type

* **Type:** string
* **Purpose:** Declares the evaluation strategy used by the rule
* **Examples:**

  * `THRESHOLD`
  * `RANGE`
  * `DURATION`
  * `COUNT`

⚠️ Must correspond to a registered rule type in the Rule Type Registry.

---

#### spec.input

* **Type:** string
* **Purpose:** Name of the evidence metric or derived field evaluated by the rule
* **Examples:**

  * `speed_over_limit_seconds`
  * `lane_departure_count`

---

#### spec.operator

* **Type:** enum
* **Allowed Values:**

  * `<`
  * `<=`
  * `>`
  * `>=`
  * `==`
  * `!=`
* **Purpose:** Comparison operator applied during rule evaluation

---

#### spec.value

* **Type:** number | string | boolean
* **Purpose:** Literal value used for comparison during evaluation

---

## Semantic Constraints (Compiler-Enforced)

The following rules are enforced by the ingestion compiler and **not** by JSON Schema:

* `type` must be registered in the Rule Type Registry
* `input` must exist in the Evidence Schema
* `operator` must be valid for the given rule type
* `value` must be compatible with the input’s data type
* Rules must be deterministic and side-effect free

---

## Example: Threshold Rule

```json
{
  "kind": "Rule",
  "id": "speed_threshold_rule",
  "version": 1,
  "spec": {
    "type": "THRESHOLD",
    "input": "speed_over_limit_seconds",
    "operator": "<=",
    "value": 10
  }
}
```

**Meaning:**
The total time spent exceeding the speed limit must be less than or equal to 10 seconds.

---

## Design Rationale

Rules are intentionally:

* Simple
* Typed
* Parameterized

This design avoids:

* Expression parsing
* Injection risks
* Non-deterministic behavior
* Debugging complexity

More complex logic is achieved through **Rulesets**, not individual rules.

---

## One Line to Remember

> **A Rule is a typed predicate over evidence.**
