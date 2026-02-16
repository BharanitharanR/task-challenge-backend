package com.banyan.platform.ast.node;

import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.platform.runtime.EvidenceContext;

public final class RuleExecutableNode implements ExecutableNode {

    private final CompiledRule rule;

    public RuleExecutableNode(CompiledRule rule) {
        this.rule = rule;
    }

    @Override
    public boolean evaluate(EvidenceContext context) {

        Object actual = context.get(rule.input());

        if (actual == null) {
            throw new IllegalArgumentException(
                    "Missing evidence: " + rule.input()
            );
        }

        return applyOperator(actual, rule.operator(), rule.value());
    }

    private boolean applyOperator(
            Object actual,
            String operator,
            Object expected
    ) {

        switch (operator) {

            case ">":
                return ((Number) actual).doubleValue() >
                        (Double.valueOf((String) expected)).doubleValue();

            case "<":
                return ((Number) actual).doubleValue() <
                        (Double.valueOf((String) expected)).doubleValue();

            case ">=":
                return ((Number) actual).doubleValue() >=
                        (Double.valueOf((String) expected)).doubleValue();

            case "<=":
                return ((Number) actual).doubleValue() <=
                        (Double.valueOf((String) expected)).doubleValue();

            case "==":
                return actual.equals(expected);

            case "!=":
                return !actual.equals(expected);

            default:
                throw new IllegalStateException(
                        "Unsupported operator: " + operator
                );
        }
    }
}
