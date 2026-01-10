package com.banyan.compiler.semantics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RuleSetSemanticValidator implements SemanticValidator {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Set<String> ALLOWED_OPERATORS = Set.of("AND", "OR");

    @Override
    public List<String> validate(String dslJson) {
        try {
            List<String> violations = new ArrayList<>();
            JsonNode root = mapper.readTree(dslJson);
            JsonNode expression = root.at("/spec/expression");

            if (expression.isMissingNode()) {
                violations.add("RULESET_ERR 30001: Missing expression in ruleset");
                return violations;
            }

            validateExpression(expression, violations);
            return violations;

        } catch (Exception e) {
            return List.of(
                    "RULESET_SEMANTIC_PARSER_ERROR: " +
                            (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
            );
        }
    }

    private void validateExpression(JsonNode node, List<String> violations) {

        // Case 1: ruleRef node
        if (node.has("ruleRef")) {
            if (!node.get("ruleRef").isTextual()
                    || node.get("ruleRef").asText().isBlank()) {
                violations.add(
                        "RULESET_ERR 30002: ruleRef must be a non-empty string"
                );
            }
            return;
        }

        // Case 2: logical node
        if (node.has("operator") && node.has("operands")) {

            String operator = node.get("operator").asText(null);
            if (!ALLOWED_OPERATORS.contains(operator)) {
                violations.add(
                        "RULESET_ERR 30003: Invalid logical operator: " + operator
                );
            }

            JsonNode operands = node.get("operands");
            if (!operands.isArray() || operands.size() < 2) {
                violations.add(
                        "RULESET_ERR 30004: Logical operator must have at least 2 operands"
                );
                return;
            }

            for (JsonNode operand : operands) {
                validateExpression(operand, violations);
            }
            return;
        }

        // Invalid node
        violations.add(
                "RULESET_ERR 30005: Invalid expression node structure"
        );
    }
}
