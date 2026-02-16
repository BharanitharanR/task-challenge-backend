# Project Banyan

Banyan is a **compiler + runtime platform** for deterministic rule evaluation. You author
governance logic as DSL files, compile them into portable **DAR artifacts**, and execute them
with evidence at runtime—without shipping business logic in code.

## What it does

- **Compile** DSL sources into a deterministic DAR package.
- **Execute** the DAR against evidence to get a replayable result.
- **Separate concerns** so runtime never interprets DSL semantics.

## Quick example (driving rules)

The runtime can load and evaluate the sample **`compilation_driving_package.dar`**:

```java
DarRuntimeContext context =
    ZipDarLoader.load("./src/main/resources/compilation_driving_package.dar");

var rulesetKey = context.rulesets().iterator().next().getKey();
ExecutableNode root = new AstBuilder(context).build(rulesetKey);

EvidenceContext evidence = new EvidenceContext(Map.of(
    "speedOverLimitSeconds", 500000,
    "laneDepartureCount", 9,
    "country", "IN"
));

boolean result = root.evaluate(evidence);
```

## Repository layout

- `project-banyan-compiler/` — DSL compiler and DAR emitter.
- `project-banyan-runtime/` — Runtime evaluator for compiled DARs.
- `docs/` — Platform specifications and architecture notes.

## Learn more

- Compiler: [`project-banyan-compiler/README.md`](project-banyan-compiler/README.md)
- Runtime: [`project-banyan-runtime/README.md`](project-banyan-runtime/README.md)
- DAR spec: [`docs/DAR_SPEC.md`]([AstBuilderTest.java](project-banyan-runtime/src/test/java/com/banyan/platform/ast/builder/AstBuilderTest.java)docs/DAR_SPEC.md)
