package com.banyan.compiler.backend.ruleset;

import com.banyan.compiler.enums.LogicalOperator;

import java.util.List;

public record LogicalNode(
        LogicalOperator operator,
        List<RulesetExpression> operands
) implements RulesetExpression {}
