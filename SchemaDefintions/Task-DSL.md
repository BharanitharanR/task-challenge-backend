# Task Definition DSL

## Overview

A Task is the most fundamental building block of the platform.  
It represents what is evaluated, not how it is evaluated.

Tasks are:

- Atomic
- Passive
- Immutable once versioned
- Reusable across challenges

### Structure
```json
{
  "kind": "Task",
  "id": "<string>",
  "version": <integer>,
  "spec": {
    "name": "<string>",
    "description": "<string>",
    "evidenceType": "<string>",
    "resultType": "<enum>"
  }
}
```

#### Field Definitions

##### kind
- **Type:** string  
- **Value:** Must be "Task"  
- **Purpose:** Identifies the definition type

##### id
- **Type:** string  
- **Constraints:** Alphanumeric, underscores, hyphens; Must be globally unique  
- **Purpose:** Canonical identifier for the task

##### version
- **Type:** integer  
- **Constraints:** Must be ≥ 1; Immutable once published  
- **Purpose:** Enables versioned evolution of the task

##### spec
Container for task-specific attributes.

###### spec.name
- **Type:** string  
- **Purpose:** Human-readable task name

###### spec.description
- **Type:** string  
- **Purpose:** Detailed explanation of what the task represents  
- **Note:** This field is for documentation and auditability

###### spec.evidenceType
- **Type:** string  
- **Purpose:** Describes the category of evidence this task evaluates  
- **Examples:** DRIVING_METRIC, USER_ACTION, SYSTEM_EVENT  
- ⚠️ Must correspond to a registered evidence type in the platform.

###### spec.resultType
- **Type:** enum  
- **Allowed Values:** BOOLEAN, NUMBER, SCORE, DURATION  
- **Purpose:** Declares the output type of task evaluation

### Semantic Constraints (Compiler-Enforced)
These rules are not enforced by JSON Schema:

- `evidenceType` must exist in the Evidence Registry
- Tasks must not define logic, thresholds, or scoring
- Tasks are not executable on their own
- Tasks can be reused across multiple challenges

### Example
```json
{
  "kind": "Task",
  "id": "maintain_speed_limit",
  "version": 1,
  "spec": {
    "name": "Maintain Speed Limit",
    "description": "Ensures the driver does not exceed the posted speed limit during a trip.",
    "evidenceType": "DRIVING_METRIC",
    "resultType": "BOOLEAN"
  }
}
```

