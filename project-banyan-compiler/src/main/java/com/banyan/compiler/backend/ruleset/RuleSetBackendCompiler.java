package com.banyan.compiler.backend.ruleset;

import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.spi.AbstractBackendCompiler;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.LogicalOperator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public final class RuleSetBackendCompiler
            extends AbstractBackendCompiler<CompiledRulesetArtifact> {

    @Override
    public CompiledRulesetArtifact compile(JsonNode dsl, CompilationContext context) {

        String id = readId(dsl);
        int version = readVersion(dsl);
        CompilationMetadata metadata = metadata(dsl);

        JsonNode spec = dsl.at("/spec");

        RulesetExpression root;

        // -------- Case 1: Explicit expression --------
        if (spec.has("expression")) {
            root = parseExpression(spec.get("expression"));
        }
        // -------- Case 2: Implicit single-rule ruleset --------
        else if (spec.has("ruleRef")) {
            root = new RuleRefNode(spec.get("ruleRef").asText());
        }
        else {
            throw new IllegalStateException(
                    "RULESET_COMPILATION_ERROR: spec must contain 'expression' or 'ruleRef'"
            );
        }

        return new CompiledRulesetArtifact(
                id,
                version,
                new CompiledRuleset(root),
                metadata
        );
    }

    // ---------------- Expression parsing ----------------

    private RulesetExpression parseExpression(JsonNode expr) {

        // Leaf rule reference
        if (expr.has("ruleRef")) {
            return new RuleRefNode(expr.get("ruleRef").asText());
        }

        // Composite node
        if (expr.has("operator") && expr.has("operands")) {

            LogicalOperator operator =
                    LogicalOperator.valueOf(expr.get("operator").asText());

            List<RulesetExpression> operands = new ArrayList<>();
            for (JsonNode operand : expr.get("operands")) {
                operands.add(parseExpression(operand));
            }

            return new LogicalNode(operator, operands);
        }

        throw new IllegalStateException(
                "INVALID_RULESET_EXPRESSION: " + expr.toString()
        );
    }
}