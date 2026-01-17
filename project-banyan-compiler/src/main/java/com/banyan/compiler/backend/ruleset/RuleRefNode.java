package com.banyan.compiler.backend.ruleset;

public record RuleRefNode(
        String ruleId
) implements RulesetExpression {}
