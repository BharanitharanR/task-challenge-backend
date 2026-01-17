package com.banyan.compiler.backend.rule;

public record CompiledRule(
        String input,      // e.g. "failedAttempts"
        String operator,   // e.g. ">"
        Object value,      // e.g. 3 / true / "LOCKED"
        String ruleType    // e.g. "THRESHOLD", "EQUALITY"
) {}
